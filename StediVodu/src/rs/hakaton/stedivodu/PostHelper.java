package rs.hakaton.stedivodu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.util.Log;


public class PostHelper {
	

	private static final String TAG="PostHelper";
	
	public static String post(Context c, String... params) {
		String action = params[0];
		
		String queryString = "/";
		
		if(params.length>1) {
			for (int i=1; i<params.length; i++) {
				queryString += params[i] + "/";
			}
		}

//		System.out.println("*** doInBackground ** paramUsername "
//				+ paramUsername + " paramPassword :" + paramPassword);

		HttpClient httpClient = new DefaultHttpClient();

		// In a POST request, we don't pass the values in the URL.
		// Therefore we use only the web page URL as the parameter of
		// the HttpPost argument
		HttpPost httpPost = new HttpPost(
				c.getString(R.string.server)+ action + queryString);
		
		Log.d(TAG, "Calling handler=" + c.getString(R.string.server)+ action + queryString);

		// Because we are not passing values over the URL, we should
		// have a mechanism to pass the values that can be
		// uniquely separate by the other end.
		// To achieve that we use BasicNameValuePair
		// Things we need to pass with the POST request
		BasicNameValuePair usernameBasicNameValuePair = new BasicNameValuePair(
				"username", c.getString(R.string.username));
		BasicNameValuePair passwordBasicNameValuePAir = new BasicNameValuePair(
				"password", c.getString(R.string.password));
		
		

		// We add the content that we want to pass with the POST request
		// to as name-value pairs
		// Now we put those sending details to an ArrayList with type
		// safe of NameValuePair
		List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
		nameValuePairList.add(usernameBasicNameValuePair);
		nameValuePairList.add(passwordBasicNameValuePAir);
		
		if (params[1].equals("setuserdata")) {
			
//			grad = $params[0], 
//			racun = $params[1],
//                        tip_racuna = $params[2],
//                        kucastan = $params[3]
			
			BasicNameValuePair gradBasicNameValuePAir = null;
			gradBasicNameValuePAir = new BasicNameValuePair(
					"grad", User.grad == null? "Nepoznani grad" : User.grad );			
			BasicNameValuePair racunBasicNameValuePAir = new BasicNameValuePair(
					"racun", String.valueOf(User.racun) );
			BasicNameValuePair tipRacunadBasicNameValuePAir = new BasicNameValuePair(
					"tip_racuna", User.tipRacuna);
			BasicNameValuePair kucaStanBasicNameValuePAir = new BasicNameValuePair(
					"kucastan", User.jeKuca ? "kuca" : "stan");
			BasicNameValuePair userIdBasicNameValuePAir = new BasicNameValuePair(
					"id_korisnik", User.userId);
			
			nameValuePairList.add(gradBasicNameValuePAir);
			nameValuePairList.add(racunBasicNameValuePAir);
			nameValuePairList.add(tipRacunadBasicNameValuePAir);
			nameValuePairList.add(kucaStanBasicNameValuePAir);			
			nameValuePairList.add(userIdBasicNameValuePAir);
		}

		try {
			// UrlEncodedFormEntity is an entity composed of a list of
			// url-encoded pairs.
			// This is typically useful while sending an HTTP POST
			// request.
			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					nameValuePairList);

			// setEntity() hands the entity (here it is
			// urlEncodedFormEntity) to the request.
			httpPost.setEntity(urlEncodedFormEntity);

			try {
				// HttpResponse is an interface just like HttpPost.
				// Therefore we can't initialize them
				HttpResponse httpResponse = httpClient
						.execute(httpPost);

				// According to the JAVA API, InputStream constructor do
				// nothing.
				// So we can't initialize InputStream although it is not
				// an interface
				InputStream inputStream = httpResponse.getEntity()
						.getContent();

				InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream);

				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);

				StringBuilder stringBuilder = new StringBuilder();

				String bufferedStrChunk = null;

				while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
					stringBuilder.append(bufferedStrChunk);
				}

				return stringBuilder.toString();

			} catch (ClientProtocolException cpe) {
				System.out
						.println("First Exception caz of HttpResponese :"
								+ cpe);
				cpe.printStackTrace();
			} catch (IOException ioe) {
				System.out
						.println("Second Exception caz of HttpResponse :"
								+ ioe);
				ioe.printStackTrace();
			}

		} catch (UnsupportedEncodingException uee) {
			System.out
					.println("An Exception given because of UrlEncodedFormEntity argument :"
							+ uee);
			uee.printStackTrace();
		}

		return null;
	}
	
	
}
