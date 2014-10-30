package cuina.legion.client.gui.connect;

import cuina.legion.client.Client;
import cuina.legion.shared.logger.LegionLogger;
import cuina.legion.shared.logger.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CertificateController
{

	private static final char[]           HEXDIGITS   = "0123456789ABCDEF".toCharArray();
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.YYYY");

	private static MessageDigest SHA1;

	static
	{
		try
		{
			CertificateController.SHA1 = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e)
		{
		}
	}

	@FXML
	private Label certificateQuestion;

	@FXML
	private Label certificateName;

	@FXML
	private Label certificateTrust;

	@FXML
	private Label subject;
	@FXML
	private Label sha1;
	@FXML
	private Label validBegin;
	@FXML
	private Label validEnd;

	@FXML
	private Button decline;
	@FXML
	private Button accept;

	private KeyStore        keystore;
	private String          keystoreFile;
	private X509Certificate cert;
	private char[]          passphrase;

	private static String toHexString(byte[] bytes)
	{
		StringBuilder sb = new StringBuilder(bytes.length * 3);
		for(int i = 0; i < bytes.length; i++)
		{
			int b = bytes[i];
			b &= 0xff;
			sb.append(CertificateController.HEXDIGITS[b >> 4]);
			sb.append(CertificateController.HEXDIGITS[b & 15]);
			if(i < bytes.length - 1)
			{
				sb.append(":");
			}
		}
		return sb.toString();
	}

	public synchronized void setCertificateData(final String serverName,
			final X509Certificate cert, KeyStore keystore, String keyStoreFile, char[] passphrase)
	{
		this.cert = cert;
		this.keystore = keystore;
		this.keystoreFile = keyStoreFile;
		this.passphrase = passphrase;

		Platform.runLater(() -> {
			try
			{
				CertificateController.this.certificateQuestion
						.setText(CertificateController.this.certificateQuestion.getText()
								.replace("%server%", serverName));
				CertificateController.this.certificateName
						.setText(CertificateController.this.certificateName.getText().replace(
								"%server%", serverName));

				CertificateController.this.subject.setText(cert.getSubjectX500Principal()
						.toString().replace(", ", "\n"));

				CertificateController.SHA1.update(cert.getEncoded());

				CertificateController.this.sha1.setText("Fingerabdruck (SHA1): "
						+ CertificateController.toHexString(CertificateController.SHA1.digest()));
				CertificateController.this.validBegin.setText("Ausstellungsdatum: "
						+ CertificateController.DATE_FORMAT.format(cert.getNotBefore()));
				CertificateController.this.validEnd.setText("Ablaufdatum: "
						+ CertificateController.DATE_FORMAT.format(cert.getNotAfter()));
			} catch (CertificateEncodingException e)
			{
				Logger.exception(LegionLogger.STDERR, e);
			}

		});
	}

	public synchronized void setCertificateExpired(final String serverName,
			final X509Certificate cert)
	{
		CertificateController.this.accept.setDisable(true);

		try
		{
			CertificateController.this.certificateQuestion.setText("Zertifikat für " + serverName
					+ " konnte nicht validiert werden.");
			CertificateController.this.certificateName.setText("Das Zertifikat für " + serverName
					+ " ist nicht gültig!");
			CertificateController.this.subject.setText(cert.getSubjectX500Principal().toString()
					.replace(", ", "\n"));
			CertificateController.this.certificateTrust.setText("Das aktuelle Datum ("
					+ CertificateController.DATE_FORMAT.format(new Date())
					+ ") liegt außerhalb des Gültigkeitszeitraums des Zertifikats.");

			CertificateController.SHA1.update(cert.getEncoded());

			CertificateController.this.sha1.setText("Fingerabdruck (SHA1): "
					+ CertificateController.toHexString(CertificateController.SHA1.digest()));
			CertificateController.this.validBegin.setText("Ausstellungsdatum: "
					+ CertificateController.DATE_FORMAT.format(cert.getNotBefore()));
			CertificateController.this.validEnd.setText("Ablaufdatum: "
					+ CertificateController.DATE_FORMAT.format(cert.getNotAfter()));
		} catch (CertificateEncodingException e)
		{
			Logger.exception(LegionLogger.STDERR, e);
		}

	}

	@FXML
	private void decline() throws SocketException, IOException
	{
		Client.getCommunicator().close();
		Platform.exit();
	}

	@FXML
	private void accept() throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
			IOException
	{
		this.keystore.setCertificateEntry(this.cert.getSubjectDN().getName(), this.cert);
		OutputStream out = new FileOutputStream(this.keystoreFile);
		this.keystore.store(out, this.passphrase);
		out.close();
		Client.getCommunicator();
		Client.getFxController().loadMask("/cuina/legion/client/fxml/connect/Connect.fxml");
	}
}
