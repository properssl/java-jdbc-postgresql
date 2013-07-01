package org.properssl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.UUID;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class SingleCertTrustManager implements X509TrustManager {
	X509TrustManager trustManager;
	X509Certificate cert;

	public SingleCertTrustManager(InputStream in) {
		init(in);
	}

	public SingleCertTrustManager(String certificatePem) {
		init(new ByteArrayInputStream(certificatePem.getBytes()));
	}

	private void init(InputStream in) {
		try {
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			try {
				// Note: KeyStore requires it be loaded even if you don't load
				// anything into it:
				ks.load(null);
			} catch (Exception e) {
			}
			CertificateFactory cf = CertificateFactory.getInstance("X509");
			cert = (X509Certificate) cf.generateCertificate(in);
			ks.setCertificateEntry(UUID.randomUUID().toString(), cert);
			TrustManagerFactory tmf = TrustManagerFactory
					.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(ks);
			for (TrustManager tm : tmf.getTrustManagers()) {
				if (tm instanceof X509TrustManager) {
					trustManager = (X509TrustManager) tm;
					break;
				}
			}
			if (trustManager == null) {
				throw new RuntimeException("No X509TrustManager found");
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		trustManager.checkClientTrusted(chain, authType);
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		trustManager.checkServerTrusted(chain, authType);
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[] { cert };
	}
}