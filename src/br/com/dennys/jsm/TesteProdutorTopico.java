package br.com.dennys.jsm;

import java.io.StringWriter;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import br.com.dennys.model.Pedido;
import br.com.dennys.model.PedidoFactory;

//Este código é para receber uma MENSAGEM! Específica! Para ficar on-line recebendo as msgs de forma instantânea ver o restante
//do projeto.
public class TesteProdutorTopico {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		
		
		InitialContext contextAmq = new InitialContext();
		//O nome do lookup "ConnectionFactory" é específico do AMQ, cada MOM tem o seu.
		ConnectionFactory factory = (ConnectionFactory) contextAmq.lookup("ConnectionFactory");
	
		Connection connection = factory.createConnection();
		connection.start();
		
		//Aqui é para criar o consumidor de mensagens no AMQ
		//O boolean false é para não criar uma transação, para facilitar foi usado false
		//Caso quisesse usar uma transação, existe um método chamado session.commit que faz isso.
		//O Session.AUTO_ACKNOWLEDGE diz que queremos automaticamente 
		//(através da Session) confirmar o recebimento da mensagem JMS. 
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		//Nome da fila configurado no jndi.properties
		Destination topicoLoja = (Destination) contextAmq.lookup("loja");

		//MessageProducer producer = session.createProducer(filaFinanceiro);
		MessageProducer producer = session.createProducer(topicoLoja);
		
		Pedido pedido = new PedidoFactory().geraPedidoComValores();
		
//      ***--------ESCREVER UM XML A PARTIR DE UM OBJETO------------------------****
		StringWriter xmlWriter = new StringWriter();
		JAXB.marshal(pedido, xmlWriter);
		String xml = xmlWriter.toString();
		
		//Obs.1: Para funcionar, a classe modelo precisa das anotações: @XmlRootElement  e @XmlAccessorType(XmlAccessType.FIELD)
		//Obs.2: Observe que a classe pedido tem um array de Itens, desta forma precisa da anotação @XmlElementWrapper(name="itens") e @XmlElement(name="item")
		//Obs.3: A classe Item, por ser usada dentro da classe pedido, deve possuir a anotação @XmlAccessorType. 
//      ***---------------------------------------------------------------------****
		
		//ENVIO DE XML COMO MENSAGEM
//		Message mensagem = session.createTextMessage(xml);
//		mensagem.setJMSType("estoque");
//		mensagem.setBooleanProperty("validade", true);
//		producer.send(mensagem);
		
		//ENVIO DO PRÓPRIO OBJETO COMO MENSAGEM
		Message mensagem2 = session.createObjectMessage(pedido);
		mensagem2.setJMSType("estoque");
		mensagem2.setBooleanProperty("validade", true);
		producer.send(mensagem2);
		
		
		
//		for (int i = 0; i < 20; i++) {
//			if (i%2 == 0 ) {
//				Message mensagem = session.createTextMessage("Pedido n. " + i + " para o ESTOQUE");
//				mensagem.setJMSType("estoque");
//				mensagem.setBooleanProperty("validade", true);
//				producer.send(mensagem);
//				System.out.println("Pedido " + i + " com validade igual a true enviado para ESTOQUE.");
//			}else {
//-----***Bloco de teste-----------****			
//				Message mensagem = session.createTextMessage("Pedido n. " + i + " para o ESTOQUE");
//				mensagem.setJMSType("estoque");
//				mensagem.setBooleanProperty("validade", false);
//				producer.send(mensagem);
//				System.out.println("Pedido " + i + " com validade igual a FALSE enviado para ESTOQUE.");
//-----***Fim bloco de teste-----------****					
//				
//				Message mensagem = session.createTextMessage("Pedido n. " + i + " para o RH");
//				mensagem.setJMSType("rh");
//				producer.send(mensagem);
//				System.out.println("Pedido " + i + " enviado para RH.");
//			}
//			
//		}
		
		//producer.send(mensagem);
		//new Scanner(System.in).nextLine();
		
		session.close();
		connection.close();
		contextAmq.close();
		
	}

}
