package ru.klodmit.s21_community_bot.services;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class CheckSchoolAccount {
//TODO: REMAKE IT with @FeignClient
    @Value("${school.username}")
    private String username;
    @Value("${school.password}")
    private String password;

    public String getAccessToken() {
        try {
            URL url = new URL("https://auth.sberclass.ru/auth/realms/EduPowerKeycloak/protocol/openid-connect/token");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);

            String parameters = "username=" + username +
                    "&password=" + password +
                    "&grant_type=password" +
                    "&client_id=s21-open-api";
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = parameters.getBytes("UTF-8");
                os.write(input, 0, input.length);
            }
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    return jsonResponse.getString("access_token");
                }
            } else {
                System.out.println("Ошибка авторизации: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ5V29landCTmxROWtQVEpFZnFpVzRrc181Mk1KTWkwUHl2RHNKNlgzdlFZIn0.eyJleHAiOjE3MzEwMjkwMzksImlhdCI6MTczMDk5MzAzOSwianRpIjoiM2EwNTk5YjUtZTAzYS00NGJkLWFiYjYtOWJjMDAyZjJmNDMyIiwiaXNzIjoiaHR0cHM6Ly9hdXRoLnNiZXJjbGFzcy5ydS9hdXRoL3JlYWxtcy9FZHVQb3dlcktleWNsb2FrIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6IjYyNTUxNmRhLTU0ZTMtNDZmMC04MWU5LWI3MjY0MjM3Yzk0YSIsInR5cCI6IkJlYXJlciIsImF6cCI6InMyMS1vcGVuLWFwaSIsInNlc3Npb25fc3RhdGUiOiJlZjRjNmYwNi1lNTFmLTQwMzEtYTM5My0yOWI1ZDA3NjFiZDciLCJhY3IiOiIxIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHBzOi8vZWR1LjIxLXNjaG9vbC5ydSJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy1lZHVwb3dlcmtleWNsb2FrIiwib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJ1c2VyX2lkIjoiMjRjODBhZGEtZjM3NS00ZWNhLWI2YjMtYjI0ZDQ0YzRhMDk4IiwibmFtZSI6IlJoYWVsbGEgQXZlbmdlciIsImF1dGhfdHlwZV9jb2RlIjoiZGVmYXVsdCIsInByZWZlcnJlZF91c2VybmFtZSI6InJoYWVsbGFhQHN0dWRlbnQuMjEtc2Nob29sLnJ1IiwiZ2l2ZW5fbmFtZSI6IlJoYWVsbGEiLCJmYW1pbHlfbmFtZSI6IkF2ZW5nZXIiLCJlbWFpbCI6InJoYWVsbGFhQHN0dWRlbnQuMjEtc2Nob29sLnJ1In0.qr9P_lU_6WHEkuyiIZCh5GOLyOIQddgQpVyZB-s9ncmyY_g9lgbpLyh3d2WkH75Eb3Weg3GbnGqmUkw3Znu_zlImHTovTx3Hho79SzWFwX3jLbiDvS1TnKt7riaWXzoYDTmITaEZM0vUTHVr0QCaIh4MNfDxanwOjQYGcU-PxQo1fbGe2sGI0Ct18qVxYO0w-aWMfD-3e1dsi4pcpzWBHzE1Uh7SwuMpwhJw9xfcr9P0dqDfW2cNffHYWb3a8Gxqd_hTCrRG8R6DpPa_9v2c6hK3e6XdJMBEzIMYmSv4se-vbeb8XNF5O4cidGZwnqGYvtdva5bv4A73oTGZasURFg";

    public String getUserStatus(String username) {
        try {
            token = getAccessToken();
            System.out.println(token);
            URL url = new URL("https://edu-api.21-school.ru/services/21-school/api/v1/participants/" + username + "@student.21-school.ru");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + token);

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    JSONObject jsonResponse = new JSONObject(response.toString());

                    if(jsonResponse.get("status")==null){
                        return "ERROR";
                    } else{
                        return jsonResponse.getString("status");
                    }
                }
            } else if (responseCode == 404) {
                return "NOT_FOUND";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ERROR";
    }
    public String getUserProgram(String username) {
        try {
            System.out.println(token);
            URL url = new URL("https://edu-api.21-school.ru/services/21-school/api/v1/participants/" + username + "@student.21-school.ru");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + token);

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    if(jsonResponse.get("parallelName") == null){
                        return "ERROR";
                    } else {
                        return jsonResponse.get("parallelName").toString();
                    }
                }
            } else if (responseCode == 404) {
                return "NOT_FOUND";
            } else if (responseCode == 401 || responseCode == 400){
                token = getAccessToken();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ERROR";
    }
}
