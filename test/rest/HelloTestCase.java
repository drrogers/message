package rest;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import junit.framework.TestCase;

import org.glassfish.jersey.client.ClientConfig;
import org.junit.Before;

public class HelloTestCase extends TestCase {

	ClientConfig config;
	Client client;
	WebTarget webTarget;

	@Before
	public void setUp() {
		config = new ClientConfig();
		client = ClientBuilder.newClient(config);
		webTarget = client.target("http://localhost:8080/grogers.message/rest");
	}
	
	public void testTextPlain() {
		Response response = webTarget.path("hello").request(MediaType.TEXT_PLAIN_TYPE).get();
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		String content = response.readEntity(String.class).trim();
		assertFalse(content.startsWith("<?xml"));
		assertFalse(content.startsWith("<html"));
		assertTrue(content.contains("Hello Jersey"));
	}

	public void testTextXML() {
		Response response = webTarget.path("hello").request(MediaType.TEXT_XML).get();
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		String content = response.readEntity(String.class).trim();
		assertTrue(content.startsWith("<?xml"));
		assertTrue(content.contains("Hello Jersey"));
	}
	
	public void testTextHTML() {
		Response response = webTarget.path("hello").request(MediaType.TEXT_HTML).get();
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		String content = response.readEntity(String.class).trim();
		assertTrue(content.startsWith("<html"));
		assertTrue(content.contains("Hello Jersey"));
	}

} 