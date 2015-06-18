package niobe.legion.client.gui.connect;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import niobe.legion.client.Client;
import niobe.legion.shared.logger.LegionLogger;
import niobe.legion.shared.logger.Logger;

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
			SHA1 = MessageDigest.getInstance("SHA1");
		}
		catch (NoSuchAlgorithmException e)
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
		for (int i = 0; i < bytes.length; i++)
		{
			int b = bytes[i];
			b &= 0xff;
			sb.append(HEXDIGITS[b >> 4]);
			sb.append(HEXDIGITS[b & 15]);
			if (i < bytes.length - 1)
			{
				sb.append(":");
			}
		}
		return sb.toString();
	}

	public synchronized void setCertificateData(final String serverName,
												final X509Certificate cert,
												KeyStore keystore,
												String keyStoreFile,
												char[] passphrase)
	{
		this.cert = cert;
		this.keystore = keystore;
		this.keystoreFile = keyStoreFile;
		this.passphrase = passphrase;

		Platform.runLater(() -> {
			try
			{
				this.certificateQuestion.setText(this.certificateQuestion.getText().replace("%server%", serverName));
				this.certificateName.setText(this.certificateName.getText().replace("%server%", serverName));

				this.subject.setText(cert.getSubjectX500Principal().toString().replace(", ", "\n"));

				SHA1.update(cert.getEncoded());

				this.sha1.setText("Fingerabdruck (SHA1): " + toHexString(SHA1.digest()));
				this.validBegin.setText("Ausstellungsdatum: " + DATE_FORMAT.format(cert.getNotBefore()));
				this.validEnd.setText("Ablaufdatum: " + DATE_FORMAT.format(cert.getNotAfter()));
			}
			catch (CertificateEncodingException e)
			{
				Logger.exception(LegionLogger.STDERR, e);
			}

		});
	}

	public synchronized void setCertificateExpired(final String serverName, final X509Certificate cert)
	{
		Platform.runLater(() -> {
			try
			{
				this.accept.setDisable(true);

				this.certificateQuestion.setText("Zertifikat für " + serverName + " konnte nicht validiert werden.");
				this.certificateName.setText("Das Zertifikat für " + serverName + " ist nicht gültig!");
				this.subject.setText(cert.getSubjectX500Principal().toString().replace(", ", "\n"));
				this.certificateTrust.setText("Das aktuelle Datum (" + DATE_FORMAT.format(new Date()) +
											  ") liegt außerhalb des Gültigkeitszeitraums des Zertifikats.");

				SHA1.update(cert.getEncoded());

				this.sha1.setText("Fingerabdruck (SHA1): " + toHexString(SHA1.digest()));
				this.validBegin.setText("Ausstellungsdatum: " + DATE_FORMAT.format(cert.getNotBefore()));
				this.validEnd.setText("Ablaufdatum: " + DATE_FORMAT.format(cert.getNotAfter()));
			}
			catch (CertificateEncodingException e)
			{
				Logger.exception(LegionLogger.STDERR, e);
			}

		});
	}

	@FXML
	private void decline() throws SocketException, IOException
	{
		Client.getCommunicator().close();
		Platform.exit();
	}

	@FXML
	private void accept() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException
	{
		this.keystore.setCertificateEntry(this.cert.getSubjectDN().getName(), this.cert);
		OutputStream out = new FileOutputStream(this.keystoreFile);
		this.keystore.store(out, this.passphrase);
		out.close();
		Client.getCommunicator().close();
		Client.getFxController().loadMask("/niobe/legion/client/fxml/connect/Connect.fxml");
		while (!(Client.getFxController().getCurrentController() instanceof ConnectController))
		{
			try
			{
				Thread.sleep(20);
			}
			catch (InterruptedException e1)
			{
				Logger.exception(LegionLogger.STDERR, e1);
			}
		}
		Client.getCommunicator(); // reset the communicator
	}
}
