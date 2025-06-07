import pandas as pd

# Charger le CSV
df = pd.read_csv("E:/la/TP-M/datasets/hbase/Accident_Response_Times.csv")

# Générer les commandes put pour HBase
with open("E:/la/TP-M/datasets/hbase/insert_accident_response_times.hql", "w") as f:
    f.write("create 'accident_response_times', 'data'\n\n")
    for _, row in df.iterrows():
        row_key = str(row['OBJECTID'])
        f.write(f"put 'accident_response_times', '{row_key}', 'data:ARR_HOUR', '{row['ARR_HOUR']}'\n")
        f.write(f"put 'accident_response_times', '{row_key}', 'data:ARR_MIN', '{row['ARR_MIN']}'\n")
        f.write(f"put 'accident_response_times', '{row_key}', 'data:HOSP_HR', '{row['HOSP_HR']}'\n")
        f.write(f"put 'accident_response_times', '{row_key}', 'data:HOSP_MN', '{row['HOSP_MN']}'\n")
