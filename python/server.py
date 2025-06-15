import socket
import numpy as np
from joblib import load

# === Carica modello e scaler ===
model, scaler_X, scaler_y = load('mlp_torcs_model_with_scaler.joblib')

HOST = 'localhost'
PORT = 3001

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.bind((HOST, PORT))
    s.listen(1)
    print(f"[Python] In ascolto su {HOST}:{PORT}...")

    conn, addr = s.accept()
    with conn:
        print(f"[Python] Connessione da {addr}")
        while True:
            data = conn.recv(4096)
            if not data:
                print("[Python] Connessione chiusa dal client.")
                break

            data = data.decode('utf-8').strip()
            print(f"[Python] Ricevuto: {data}")

            try:
                features = list(map(float, data.split(',')))
                input_array = np.array(features).reshape(1, -1)

                # === Applica scaling ===
                input_scaled = scaler_X.transform(input_array)

                # === Predizione ===
                prediction_scaled = model.predict(input_scaled)

                # === Inverse transform della predizione ===
                prediction = scaler_y.inverse_transform(prediction_scaled.reshape(1, -1))[0]

                print(f"[Python] Predizione: {prediction}")

                # === Invia al client ===
                response = ','.join(map(str, prediction)) + '\n'
                conn.sendall(response.encode('utf-8'))
                print("[Python] Risposta inviata al client.\n")

            except Exception as e:
                print("[Python] Errore nel parsing o nella predizione:", e)
                conn.sendall(b"0.0,0.0,0.0\n")