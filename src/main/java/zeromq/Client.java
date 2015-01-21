package zeromq;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMQ.Poller;

public class Client extends Thread {

	Context context;
	Socket send;
	Socket receive;
	Poller poller;
	Boolean IsConnect = false;
	String name;
	Sender sender;
	Receiver receiver;


	static class Sender extends Thread {
		private final String name;
		private final Socket send;

		public Sender(String name, Socket send) {
			this.name = name;
			this.send = send;
			send.send(name + "has enter 2 the chat");
		}

		public void send() {
			String messageToSend = ClientForm.str;
			send.send(name + ": " + messageToSend);
		}

		public void sendExit() {
			send.send(name + " is exit from server");
		}

		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	static class Receiver extends Thread {
		private final Poller poller;
		private final Socket receive;

		public Receiver(Poller poller, Socket receive) {
			this.poller = poller;
			this.receive = receive;
		}

		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				int events = (int) poller.poll();
				if (events > 0) {
					String recvMessage = receive.recvStr(0);
					ClientForm.textArea.setText(ClientForm.textArea.getText()
							+ "\n" + recvMessage);
				}
			}
		}
	}

	public void die() {
		while (!receiver.isInterrupted())
			receiver.interrupt();
		while (!sender.isInterrupted())
			sender.interrupt();
	}

	public void run() {
		init(ClientForm.str);
	}

	public void init(String n) {
		context = ZMQ.context(1);

		send = context.socket(ZMQ.PUSH);
		send.connect("tcp://localhost:5001");

		receive = context.socket(ZMQ.SUB);
		receive.connect("tcp://localhost:5000");
		receive.subscribe("".getBytes());

		poller = new Poller(0);
		poller.register(receive, Poller.POLLIN);

		name = new String(n);
		try {
			sender = new Sender(name, send);
			//sender.start();

			receiver = new Receiver(poller, receive);
			receiver.start();

			//sender.join();
			receiver.join();
		} catch (InterruptedException e) {
		} finally {
			System.out.println("all die");
			receive.close();
			send.close();
			context.term();
		}
	}

	public void send() {
		try {
			Sender sender = new Sender(name, send);
			//sender.start();

			Receiver receiver = new Receiver(poller, receive);
			receiver.start();

			//sender.join();
			receiver.join();
		} catch (InterruptedException e) {
		} finally {
			receive.close();
			send.close();
			context.term();
		}
	}
}
