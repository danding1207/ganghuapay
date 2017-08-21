package com.mqt.ganghuazhifu.utils;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import android.content.Context;
import android.util.Base64;

public class Pkipair {
	
	public String signMsg(String signMsg,Context context) {

		String base64 = "";
		try {
			// ��Կ�ֿ�
			KeyStore ks = KeyStore.getInstance("PKCS12");

			// ��ȡ��Կ�ֿ�
//			FileInputStream ksfis = new FileInputStream("e:/tester-rsa.pfx");
			
			// ��ȡ��Կ�ֿ⣨���·����
//			String file = Pkipair.class.getResource("tester-rsa.pfx").getPath().replaceAll("%20", " ");
//			System.out.println(file);
			
			
			
//			FileInputStream ksfis = new FileInputStream(file);
			
			InputStream ksfis = context.getAssets().open("tester-rsa.pfx");
			
			
			BufferedInputStream ksbufin = new BufferedInputStream(ksfis);

			char[] keyPwd = "123456".toCharArray();
			//char[] keyPwd = "YaoJiaNiLOVE999Year".toCharArray();
			ks.load(ksbufin, keyPwd);
			// ����Կ�ֿ�õ�˽Կ
			PrivateKey priK = (PrivateKey) ks.getKey("test-alias", keyPwd);
			Signature signature = Signature.getInstance("SHA1withRSA");
			signature.initSign(priK);
			signature.update(signMsg.getBytes("utf-8"));
			base64 = Base64.encodeToString(signature.sign(), Base64.DEFAULT);
			
		} catch(FileNotFoundException e){
		}catch (Exception ex) {
			ex.printStackTrace();
		}
		return base64;
	}
	public boolean enCodeByCer( String val, String msg,Context context) {
		boolean flag = false;
		try {
			//����ļ�(���·��)
			//InputStream inStream = new FileInputStream("e:/99bill[1].cert.rsa.20140803.cer");
			
			//����ļ�(���·��)
//			String file = Pkipair.class.getResource("99bill[1].cert.rsa.20140803.cer").toURI().getPath();
//			System.out.println(file);
//			FileInputStream inStream = new FileInputStream(file);
			
			InputStream inStream = context.getAssets().open("99bill[1].cert.rsa.20140803.cer");
			
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate cert = (X509Certificate) cf.generateCertificate(inStream);
			//��ù�Կ
			PublicKey pk = cert.getPublicKey();
			//ǩ��
			Signature signature = Signature.getInstance("SHA1withRSA");
			signature.initVerify(pk);
			signature.update(val.getBytes());
			//����
			flag = signature.verify(Base64.decode(msg, Base64.DEFAULT));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
}
