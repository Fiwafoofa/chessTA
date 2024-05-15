package net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import com.google.gson.Gson;

import response.FailureResponse;

public class HttpCommunicator {

  private final String serverUrl;
  private final Gson serializer;

  public HttpCommunicator(String serverUrl) {
    this.serverUrl = serverUrl;
    serializer = new Gson();
  }

  public <T> T makeRequest(
    String method, 
    String path, 
    Object request, 
    Class<T> responseClass,
    String token
  ) throws ResponseException {
    try {
      URI uri = new URI(serverUrl + path);
      HttpURLConnection httpConn = (HttpURLConnection) uri.toURL().openConnection();
      httpConn.setRequestMethod(method);

      httpConn.setDoOutput(true);
      
      writeHeaders(httpConn, token);
      if (!method.equals("GET")) {
        writeBody(httpConn, request);
      }
      
      httpConn.connect();
      Integer responseCode = httpConn.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        return responseClass != null ? readResponse(httpConn, responseClass) : null;
      } else {
        throwResponseException(httpConn);
        return null;
      }
    } catch (IOException | URISyntaxException exception) {
      throw new ResponseException(exception.getMessage());
    }
    
  }

  private void writeHeaders(HttpURLConnection httpURLConnection, String token) {
    httpURLConnection.addRequestProperty("Content-Type", "application/json");
    if (token != null) {
      httpURLConnection.addRequestProperty("Authorization", token);
    }
  }

  private void writeBody(HttpURLConnection httpURLConnection, Object request) throws IOException {
    try (OutputStream outputStream = httpURLConnection.getOutputStream()) {
      String jsonBody = serializer.toJson(request);
      outputStream.write(jsonBody.getBytes());
    }
  }

  private <T> T readResponse(HttpURLConnection httpURLConnection, Class<T> responseClass) throws IOException {
    try (InputStream responseBody = httpURLConnection.getInputStream()) {
      InputStreamReader inputStreamReader = new InputStreamReader(responseBody);
      T response = serializer.fromJson(inputStreamReader, responseClass);
      return response;
    }
  }

  private void throwResponseException(HttpURLConnection httpURLConnection) throws ResponseException, IOException {
    try (InputStream errorResponseBody = httpURLConnection.getErrorStream()) {
      InputStreamReader inputStreamReader = new InputStreamReader(errorResponseBody);
      BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
      StringBuilder stringBuilder = new StringBuilder();
      String line = "";
      do {
        stringBuilder.append(line);
        line = bufferedReader.readLine();
      } while (line != null);

      FailureResponse failureResponse = serializer.fromJson(stringBuilder.toString(), FailureResponse.class);
      throw new ResponseException(failureResponse.getMessage());
    }
  }
}
