import jaydebeapi
import pandas as pd

jdbc_url = "jdbc:hive2://localhost:10000/default"
driver_class = "org.apache.hive.jdbc.HiveDriver"
jar_path = "/vagrant/lib/hive-jdbc-3.1.2-standalone.jar"
username = "oracle"
password = "welcome1"  

conn = jaydebeapi.connect(
    driver_class,
    jdbc_url,
    [username, password],
    jar_path
)

cursor = conn.cursor()

def run_query(title, sql):
    print(f"\n--- {title} ---")
    cursor.execute(sql)
    results = cursor.fetchall()
    columns = [desc[0] for desc in cursor.description]
    df = pd.DataFrame(results, columns=columns)
    print(df.head(10))  # Affiche les 10 premiers résultats

# 1. Nombre d’accidents mortels selon le type de route
run_query("1. Nombre d'accidents par type de route", """
SELECT r.ROUTENAME, COUNT(*) AS total_accidents
FROM Accidents_Core_Facts a
JOIN Accident_Road_Characteristics r ON a.OBJECTID = r.OBJECTID
GROUP BY r.ROUTENAME
ORDER BY total_accidents DESC
""")

# 2. Moyenne de véhicules impliqués par classification fonctionnelle de la route
run_query("2. Moyenne de véhicules par classification fonctionnelle", """
SELECT r.FUNC_SYSNAME, AVG(a.VE_TOTAL) AS avg_vehicles
FROM Accidents_Core_Facts a
JOIN Accident_Road_Characteristics r ON a.OBJECTID = r.OBJECTID
GROUP BY r.FUNC_SYSNAME
ORDER BY avg_vehicles DESC
""")

# 3. Nombre de décès dans les zones de travaux
run_query("3. Nombre de décès par statut de zone de travaux", """
SELECT z.WRK_ZONENAME, SUM(a.FATALS) AS total_deaths
FROM Accidents_Core_Facts a
JOIN Ref_Work_Zone_Status z ON a.WRK_ZONE = z.WRK_ZONE
GROUP BY z.WRK_ZONENAME
ORDER BY total_deaths DESC
""")

# 4. Types de collisions les plus fréquents sur les routes NHS
run_query("4. Type de collision sur routes NHS", """
SELECT e.MAN_COLLNAME AS collision_type, COUNT(*) AS nb_collisions
FROM Accidents_Core_Facts a
JOIN Accident_Event_Details e ON a.OBJECTID = e.OBJECTID
JOIN Accident_Road_Characteristics r ON a.OBJECTID = r.OBJECTID
WHERE r.NHS = 1
GROUP BY e.MAN_COLLNAME
ORDER BY nb_collisions DESC
""")

# 5. Croisement de l’implication des piétons et des types d’intersections
run_query("5. Accidents avec piétons par type d’intersection", """
SELECT e.TYP_INTNAME, COUNT(*) AS accidents_avec_pietons
FROM Accidents_Core_Facts a
JOIN Accident_Event_Details e ON a.OBJECTID = e.OBJECTID
WHERE a.PEDS > 0
GROUP BY e.TYP_INTNAME
ORDER BY accidents_avec_pietons DESC
""")

# 6. Liste des accidents avec localisation, conditions météo et temps d’arrivée des secours
run_query("1. Liste des accidents avec localisation, conditions météo et temps d’arrivée des secours", """
SELECT
  loc.OBJECTID,
  loc.STATENAME,
  loc.COUNTYNAME,
  loc.CITYNAME,
  env.YEAR,
  env.MONTHNAME,
  env.WEATHERNAME,
  resp.ARR_HOUR,
  resp.ARR_MIN
FROM Accident_Locations loc
JOIN Accident_Environmental_Conditions env ON loc.OBJECTID = env.OBJECTID
JOIN Accident_Response_Times resp ON loc.OBJECTID = resp.OBJECTID
WHERE env.YEAR = 2022
LIMIT 100
""")

# 7. Temps moyen d’arrivée des secours par type de zone (Rural/Urbain)
run_query("2. Temps moyen d’arrivée des secours par type de zone (Rural/Urbain)", """
SELECT
  loc.RUR_URBNAME,
  AVG((resp.ARR_HOUR * 60 + resp.ARR_MIN) - (env.HOUR * 60 + env.MINUTE)) AS avg_response_time_minutes
FROM Accident_Locations loc
JOIN Accident_Environmental_Conditions env ON loc.OBJECTID = env.OBJECTID
JOIN Accident_Response_Times resp ON loc.OBJECTID = resp.OBJECTID
GROUP BY loc.RUR_URBNAME
""")

# 8. Nombre d’accidents par comté avec temps d’arrivée aux secours supérieur à 30 minutes
run_query("3. Nombre d’accidents par comté avec temps d’arrivée aux secours supérieur à 30 minutes", """
SELECT
  loc.COUNTYNAME,
  COUNT(*) AS nb_accidents
FROM Accident_Locations loc
JOIN Accident_Environmental_Conditions env ON loc.OBJECTID = env.OBJECTID
JOIN Accident_Response_Times resp ON loc.OBJECTID = resp.OBJECTID
WHERE ((resp.ARR_HOUR * 60 + resp.ARR_MIN) - (env.HOUR * 60 + env.MINUTE)) > 30
GROUP BY loc.COUNTYNAME
ORDER BY nb_accidents DESC
""")

# 9. Distribution des accidents selon les conditions d’éclairage et temps de réponse
run_query("4. Distribution des accidents selon les conditions d’éclairage et temps de réponse", """
SELECT
  env.LGT_CONDNAME,
  COUNT(*) AS nb_accidents,
  AVG((resp.ARR_HOUR * 60 + resp.ARR_MIN) - (env.HOUR * 60 + env.MINUTE)) AS avg_response_time
FROM Accident_Environmental_Conditions env
JOIN Accident_Locations loc ON env.OBJECTID = loc.OBJECTID
JOIN Accident_Response_Times resp ON env.OBJECTID = resp.OBJECTID
GROUP BY env.LGT_CONDNAME
ORDER BY avg_response_time DESC
""")

# 10. Analyse des temps de réponse par état et mois de l’année
run_query("5. Analyse des temps de réponse par état et mois de l’année", """
SELECT
  loc.STATENAME,
  env.MONTHNAME,
  env.MONTH,
  AVG((resp.ARR_HOUR * 60 + resp.ARR_MIN) - (env.HOUR * 60 + env.MINUTE)) AS avg_response_time_minutes
FROM Accident_Locations loc
JOIN Accident_Environmental_Conditions env ON loc.OBJECTID = env.OBJECTID
JOIN Accident_Response_Times resp ON loc.OBJECTID = resp.OBJECTID
GROUP BY loc.STATENAME, env.MONTHNAME, env.MONTH
ORDER BY loc.STATENAME, env.MONTH
""")

cursor.close()
conn.close()
