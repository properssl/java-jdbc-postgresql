package org.properssl;

import java.security.GeneralSecurityException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.postgresql.ssl.WrappedFactory;

public class SingleCertValidatingFactory extends WrappedFactory {
	public SingleCertValidatingFactory(String sslFactoryArg)
			throws GeneralSecurityException {
		SSLContext ctx = SSLContext.getInstance("TLS");
		ctx.init(null, new TrustManager[] { new SingleCertTrustManager(
				sslFactoryArg) }, null);
		_factory = ctx.getSocketFactory();
	}
}
