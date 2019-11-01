package se.ri.example;

import java.net.InetSocketAddress;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.cipher.CipherSuite;
import org.eclipse.californium.scandium.dtls.pskstore.InMemoryPskStore;

public class DtlsServer {
    
   public static class HelloWorldResource extends CoapResource {
       
       /**
        * Constructor
        */
       public HelloWorldResource() {
           
           // set resource identifier
           super("helloWorld");
           
           // set display name
           getAttributes().setTitle("Hello-World Resource");
       }

       @Override
       public void handleGET(CoapExchange exchange) {
           
           // respond to the request
           exchange.respond("Hello World!");
       }
   }
   
   private static CoapServer rs = null;
   
   public static void main(String[] args) {
       byte[] key
           = {'c', 'b', 'c', 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
     
     Resource hello = new HelloWorldResource();
   
     rs = new CoapServer();
     rs.add(hello);
     
     DtlsConnectorConfig.Builder config = new DtlsConnectorConfig.Builder()
             .setAddress(
                     new InetSocketAddress(CoAP.DEFAULT_COAP_SECURE_PORT));
       config.setSupportedCipherSuites(new CipherSuite[]{
               CipherSuite.TLS_PSK_WITH_AES_128_CCM_8});
       InMemoryPskStore psk = new InMemoryPskStore();
       psk.setKey("ourPsk", key);
       config.setPskStore(psk);
       config.setSniEnabled(false);
       DTLSConnector connector = new DTLSConnector(config.build());
       CoapEndpoint cep = new CoapEndpoint.Builder().setConnector(connector)
               .setNetworkConfig(NetworkConfig.getStandard()).build();
       rs.addEndpoint(cep);
       rs.start();
       System.out.println("Server starting");
       
   }
}
