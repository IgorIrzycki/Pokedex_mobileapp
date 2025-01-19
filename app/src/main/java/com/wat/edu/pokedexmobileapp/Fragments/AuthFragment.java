package com.wat.edu.pokedexmobileapp.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.wat.edu.pokedexmobileapp.Data.LoginRequest;
import com.wat.edu.pokedexmobileapp.Data.LoginResponse;
import com.wat.edu.pokedexmobileapp.Data.RegisterRequest;
import com.wat.edu.pokedexmobileapp.R;
import com.wat.edu.pokedexmobileapp.API.ApiClient;
import com.wat.edu.pokedexmobileapp.API.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthFragment extends Fragment {

    private EditText usernameEditText, passwordEditText, emailEditText;
    private ApiService apiService;
    private View view;  // Dodana zmienna

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Przypisujemy widok do zmiennej
        view = inflater.inflate(R.layout.fragment_auth, container, false);

        usernameEditText = view.findViewById(R.id.usernameEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        Button loginButton = view.findViewById(R.id.loginButton);
        Button registerButton = view.findViewById(R.id.registerButton);

        apiService = ApiClient.getClient().create(ApiService.class);

        loginButton.setOnClickListener(v -> handleLogin());
        registerButton.setOnClickListener(v -> handleRegister());

        return view;
    }

    private void handleLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), "Please fill in both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUserName(username);
        loginRequest.setPassword(password);

        apiService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    // Zapisz token w SharedPreferences lub innym miejscu
                    saveToken(loginResponse.getToken());
                    saveUsername(loginResponse.getUsername());

                    // Przejdź do głównego ekranu (np. HubFragment)
                    Toast.makeText(getContext(), "Login successful", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(view).navigate(R.id.action_authFragment_to_hubFragment); // Używamy 'view'
                } else {
                    Toast.makeText(getContext(), "Invalid login credentials", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Login failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Pozostałe metody
    private void saveToken(String token) {
        SharedPreferences preferences = getContext().getSharedPreferences("PokedexApp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("TOKEN", token);
        editor.apply();
    }

    private void saveUsername(String username) {
        SharedPreferences preferences = getContext().getSharedPreferences("PokedexApp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("USERNAME", username);
        editor.apply();
    }

    private void handleRegister() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isPasswordValid(password)) {
            Toast.makeText(getContext(), "Password must be at least 8 characters long, include an uppercase letter and a special character.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isEmailValid(email)) {
            Toast.makeText(getContext(), "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUserName(username);
        registerRequest.setEmail(email);
        registerRequest.setPassword(password);

        apiService.register(registerRequest).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Registration successful", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getContext(), "Registration failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 8 && password.matches(".*[A-Z].*") && password.matches(".*[!@#$%^&*].*");
    }

    private boolean isEmailValid(String email) {
        return email.matches("^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }
}

