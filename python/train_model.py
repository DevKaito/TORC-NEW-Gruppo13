import pandas as pd
import numpy as np
import time
from sklearn.neural_network import MLPRegressor
from sklearn.preprocessing import StandardScaler
from sklearn.model_selection import train_test_split
from sklearn.metrics import mean_squared_error
import joblib

# === 1. Carica CSV ===
df = pd.read_csv("dati_torcs.csv")

# === 2. Selezione colonne ===
FEATURE_COLUMNS = [
    "trackSensor_0", "trackSensor_1", "trackSensor_2", "trackSensor_3", "trackSensor_4",
    "trackSensor_5", "trackSensor_6", "trackSensor_7", "trackSensor_8", "trackSensor_9",
    "trackSensor_10", "trackSensor_11", "trackSensor_12", "trackSensor_13", "trackSensor_14",
    "trackSensor_15", "trackSensor_16", "trackSensor_17", "trackSensor_18",
    "angleToTrackAxis", "speed", "trackPosition", "gear", "lateralSpeed",
    "RPM", "wheelSpinVelocity_0", "wheelSpinVelocity_1", "wheelSpinVelocity_2", "wheelSpinVelocity_3",
    "ZSpeed", "Z"
]

TARGET_COLUMNS = ["steer", "accel", "brake"]

X = df[FEATURE_COLUMNS]
y = df[TARGET_COLUMNS]

# === 3. Standardizzazione ===
x_scaler = StandardScaler()
X_scaled = x_scaler.fit_transform(X)

y_scaler = StandardScaler()
y_scaled = y_scaler.fit_transform(y)

# === 4. Train/Test Split ===
X_train, X_test, y_train, y_test = train_test_split(X_scaled, y_scaled, test_size=0.2, random_state=42)

# === 5. Modello ===
model = MLPRegressor(
    hidden_layer_sizes=(512, 512),
    activation='relu',
    solver='adam',
    max_iter=50,
    random_state=1,
    verbose=True
)

start_time = time.time()
model.fit(X_train, y_train)
end_time = time.time()

# === 6. Valutazione ===
y_pred = model.predict(X_test)
mse = mean_squared_error(y_test, y_pred)
print(f"\nTempo totale di addestramento: {end_time - start_time:.2f} s")
print(f"Mean Squared Error (scalato): {mse:.4f}")
print(f"Model Score (R²): {model.score(X_test, y_test):.4f}")

# === 7. Salva modello e scaler ===
joblib.dump(model, "mlp_torcs_model.joblib")
joblib.dump(x_scaler, "mlp_torcs_x_scaler.joblib")
joblib.dump(y_scaler, "mlp_torcs_y_scaler.joblib")
print("Modello e scaler salvati.")

