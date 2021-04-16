package br.com.dennys.jsm;

import java.util.Scanner;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.InitialContext;

import org.apache.activemq.ActiveMQConnectionFactory;

import br.com.dennys.model.Pedido;

//Este código é para receber uma MENSAGEM! Específica! Para ficar on-line recebendo as msgs de forma instantânea ver o restante
//do projeto.
public class TesteConsumidorTopicoUtilizandoSeletorEstoque {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		
		
		InitialContext contextAmq = new InitialContext();
		//O nome do lookup "ConnectionFactory" é específico do AMQ, cada MOM tem o seu.
		//ConnectionFactory factory = (ConnectionFactory) contextAmq.lookup("ConnectionFactory");
		
		ActiveMQConnectionFactory factory = (ActiveMQConnectionFactory) contextAmq.lookup("ConnectionFactory");
		factory.setTrustAllPackages(true);
		
		Connection connection = factory.createConnection();
		connection.setClientID("dennys.estoque");
		connection.start();
		
		//Aqui é para criar o consumidor de mensagens no AMQ
		//O boolean false é para não criar uma transação, para facilitar foi usado false
		//Caso quisesse usar uma transação, existe um método chamado session.commit que faz isso.
		//O Session.AUTO_ACKNOWLEDGE diz que queremos automaticamente 
		//(através da Session) confirmar o recebimento da mensagem JMS. 
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		//Nome da fila configurado no jndi.properties
		Topic topicoLoja = (Topic) contextAmq.lookup("loja");

		//ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61617");
		
		//configurando o selector para determinar se a msg interessa ou não.
		MessageConsumer consumer = session.createDurableSubscriber(topicoLoja, "nome_do_consumer_estoque",
				"JMSType = 'estoque' AND validade = true", true);
				
		consumer.setMessageListener(new MessageListener() {
			
			@Override
			public void onMessage(Message message) {
				
				//TextMessage textoDaMensagem = (TextMessage) message;
				ObjectMessage objetoDaMensagem = (ObjectMessage) message;
				
				Pedido pedido;
				try {
					//System.out.println("Mensagem recebida: " + message);
					Pedido pedido2 = (Pedido) objetoDaMensagem.getObject();
					System.out.println("Mensagem recebida: " + pedido2.getCodigo());
				} catch (JMSException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				try {
					//System.out.println("Mensagem recebida: " + pedido.getValorTotal());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		
		
		//Message message = consumer.receive(); //essa linha faz com que o consumer receba a msg mais antiga em fila
		
		//System.out.println("Recebendo msg: " + message);
		
		new Scanner(System.in).nextLine();
		
		session.close();
		connection.close();
		contextAmq.close();
		
	}

}
