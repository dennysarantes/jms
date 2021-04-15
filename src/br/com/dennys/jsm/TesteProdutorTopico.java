package br.com.dennys.jsm;

import java.util.Iterator;
import java.util.Scanner;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

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
		
		
		
		for (int i = 0; i < 10; i++) {
			Message mensagem = session.createTextMessage("<pedido><id 222>" + i + "</id></pedido>");
			producer.send(mensagem);
			System.out.println("Pedido " + i + " enviado.");
		}
		
		//producer.send(mensagem);
		//new Scanner(System.in).nextLine();
		
		session.close();
		connection.close();
		contextAmq.close();
		
	}

}
