import pandas as pd
import time
from sklearn.neural_network import MLPRegressor
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
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

X = df[FEATURE_COLUMNS].values
y = df[TARGET_COLUMNS].values

# === 3. Scaling ===
scaler_X = StandardScaler()
scaler_y = StandardScaler()

X_scaled = scaler_X.fit_transform(X)
y_scaled = scaler_y.fit_transform(y)

# === 4. Train/Test Split ===
X_train, X_test, y_train, y_test = train_test_split(X_scaled, y_scaled, test_size=0.15, random_state=42)

# === 5. Modello ===
model = MLPRegressor(
    hidden_layer_sizes=(128, 128),
    activation='relu',
    solver='adam',
    max_iter=300,
    early_stopping=True,
    random_state=1,
    verbose=True
)

start_time = time.time()
model.fit(X_train, y_train)
end_time = time.time()

# === 6. Valutazione ===
y_pred_scaled = model.predict(X_test)
y_pred = scaler_y.inverse_transform(y_pred_scaled)
y_test_inv = scaler_y.inverse_transform(y_test)

mse = mean_squared_error(y_test_inv, y_pred)
print(f"\nTempo di addestramento: {end_time - start_time:.2f} s")
print(f"Mean Squared Error: {mse:.4f}")
print(f"Model Score (R²): {model.score(X_test, y_test):.4f}")

# === 7. Salva modello e scaler ===
joblib.dump((model, scaler_X, scaler_y), "mlp_torcs_model_with_scaler.joblib")
print("Modello e scaler salvati in 'mlp_torcs_model_with_scaler.joblib'")
