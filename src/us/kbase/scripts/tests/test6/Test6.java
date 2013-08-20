package us.kbase.scripts.tests.test6;

import us.kbase.scripts.tests.test6.authtest.AuthtestClient;

import junit.framework.Assert;

public class Test6 {
	
	public Test6(AuthtestClient client, Integer port) throws Exception {
		try {
			client.callWithAuth("");
			Assert.fail("Method shouldn't work because it requires authentication");
		} catch (Throwable ignore) {}
		Assert.assertEquals("0", client.callWithOptAuth("0"));
		client = new AuthtestClient("http://localhost:" + port, "kbasetest", "@Suite525");
		client.setAuthAllowedForHttp(true);
		Assert.assertEquals("1", client.callWithAuth("1"));
		Assert.assertEquals("2", client.callWithOptAuth("2"));
		try {
			client = new AuthtestClient("http://localhost:" + port, "token=completelywrongtoken|expiry=12345|sig=12345");
			client.setAuthAllowedForHttp(true);
			client.callWithAuth("");
			Assert.fail("Method shouldn't work because authentication should fail on server side");
		} catch (Throwable ex) {
			Assert.assertTrue(ex.getMessage().contains("JSONRPC error received"));
		}
	}
}