package se.ri.example;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.elements.Connector;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.cipher.CipherSuite;
import org.eclipse.californium.scandium.dtls.pskstore.InMemoryPskStore;

public class DtlsClient {

    public static void main(String[] args) 
            throws InterruptedException, ConnectorException, IOException {
        byte[] key
            = {'c', 'b', 'c', 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
        
        String hostname ="localhost";
  
        InetSocketAddress serverAddress = new InetSocketAddress(hostname, 
                CoAP.DEFAULT_COAP_SECURE_PORT);
        if (serverAddress == null || serverAddress.getHostString() == null) {
            throw new IllegalArgumentException(
                    "Client requires a non-null server address");
        }
       
        DtlsConnectorConfig.Builder builder 
            = new DtlsConnectorConfig.Builder().setAddress(
                    new InetSocketAddress(0));
       
        builder.setSniEnabled(false);
        builder.setSupportedCipherSuites(new CipherSuite[]{
                CipherSuite.TLS_PSK_WITH_AES_128_CCM_8});
        
        InMemoryPskStore store = new InMemoryPskStore();  
        String identity = "ourPsk";
        store.addKnownPeer(serverAddress, identity, key);
        builder.setPskStore(store);
        Connector c = new DTLSConnector(builder.build());
        CoapEndpoint e = new CoapEndpoint.Builder().setConnector(c)
                .setNetworkConfig(NetworkConfig.getStandard()).build();
        CoapClient client = new CoapClient(serverAddress.getHostString());
        client.setEndpoint(e);   
        client.setURI("coaps://localhost/helloWorld");
        CoapResponse res = client.get();
        System.out.println("Server responded: " + res.getResponseText());
    }
}