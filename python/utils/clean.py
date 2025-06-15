import pandas as pd

def clean_and_save_csv(input_path, output_path):
    # Carica CSV, considera stringhe vuote o spazi come NaN
    df = pd.read_csv(input_path, na_values=["", " "])
    
    # Stampa riepilogo NaN per colonna
    print("Conteggio valori NaN per colonna prima della pulizia:")
    print(df.isna().sum())
    
    # Stampa righe con almeno un NaN
    nan_rows = df[df.isna().any(axis=1)]
    if not nan_rows.empty:
        print(f"\nTrovate {len(nan_rows)} righe con valori NaN, ecco alcune:")
        print(nan_rows.head())
    else:
        print("\nNessuna riga con valori NaN trovata.")
    
    # Rimuove tutte le righe con almeno un NaN
    df_clean = df.dropna()
    
    print(f"\nDopo la rimozione, righe residue: {len(df_clean)}")
    
    # Salva il CSV pulito
    df_clean.to_csv(output_path, index=False)
    print(f"\nFile pulito salvato in '{output_path}'")

# Esempio di uso:
clean_and_save_csv("merged_dataset.csv", "dati_torcs.csv")

