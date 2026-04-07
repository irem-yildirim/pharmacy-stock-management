import os

cat_map = {
    'Pain relief': 1, 'Antibiotics': 2, 'Cardiology': 3, 'Gastroenterology': 4,
    'Cold & Flu': 5, 'Diabetes management': 6, 'Psychiatry & Neurology': 7,
    'Pulmonology': 8, 'Supplements': 9, 'Blood Products': 10, 'Anesthesia & Surgery': 11
}

brand_map = {
    'Menarini': 1, 'GSK': 2, 'Abdi İbrahim': 3, 'Bayer': 4, 'AstraZeneca': 5,
    'Bilim İlaç': 6, 'Viatris (Pfizer)': 7, 'Adeka': 8, 'Roche': 9, 'Deva': 10,
    'Exeltis': 11, 'Janssen': 12, 'Novartis': 13, 'Recordati': 14, 'Santa Farma': 15,
    'Sanofi': 16, 'Gerot Lannach': 17, 'Mundipharma': 18, 'İbrahim Hayri': 19,
    'Galen': 20, 'Teva': 21, 'Hameln': 22, 'Polifarma': 23, 'Takeda': 24,
    'Pfizer': 25, 'Novo Nordisk': 26, 'CSL Behring': 27, 'Octapharma': 28, 'Kedrion': 29
}

pres_map = {'White': 1, 'Green': 2, 'Red': 3, 'Orange': 4, 'Purple': 5}

drugs = [
    # White 1
    ('Arveles', 'Menarini', 'Pain relief', 'White', '86901001'),
    ('Augmentin', 'GSK', 'Antibiotics', 'White', '86901002'),
    ('Apranax', 'Abdi İbrahim', 'Pain relief', 'White', '86901003'),
    ('Coraspin', 'Bayer', 'Cardiology', 'White', '86901004'),
    ('Nexium', 'AstraZeneca', 'Gastroenterology', 'White', '86901005'),
    ('A-Ferin', 'Bilim İlaç', 'Cold & Flu', 'White', '86901006'),
    ('Glifor', 'Bilim İlaç', 'Diabetes management', 'White', '86901007'),
    ('Lustral', 'Viatris (Pfizer)', 'Psychiatry & Neurology', 'White', '86901008'),
    ('Ventolin', 'GSK', 'Pulmonology', 'White', '86901009'),
    ('Ferro Sanol', 'Adeka', 'Supplements', 'White', '86901010'),
    
    # Green 2
    ('Xanax', 'Viatris (Pfizer)', 'Psychiatry & Neurology', 'Green', '86902001'),
    ('Rivotril', 'Roche', 'Psychiatry & Neurology', 'Green', '86902002'),
    ('Diazem', 'Deva', 'Psychiatry & Neurology', 'Green', '86902003'),
    ('Lyrica', 'Viatris (Pfizer)', 'Psychiatry & Neurology', 'Green', '86902004'),
    ('Ativan', 'Exeltis', 'Psychiatry & Neurology', 'Green', '86902005'),
    ('Concerta', 'Janssen', 'Psychiatry & Neurology', 'Green', '86902006'),
    ('Ritalin', 'Novartis', 'Psychiatry & Neurology', 'Green', '86902007'),
    ('Akineton', 'Recordati', 'Psychiatry & Neurology', 'Green', '86902008'),
    ('Contramal', 'Santa Farma', 'Pain relief', 'Green', '86902009'),
    ('Stilnox', 'Sanofi', 'Psychiatry & Neurology', 'Green', '86902010'),

    # Red 3
    ('Aldolan', 'Gerot Lannach', 'Anesthesia & Surgery', 'Red', '86903001'),
    ('Durogesic', 'Janssen', 'Pain relief', 'Red', '86903002'),
    ('M-Eser', 'Mundipharma', 'Pain relief', 'Red', '86903003'),
    ('Jurnista', 'Janssen', 'Pain relief', 'Red', '86903004'),
    ('Pental', 'İbrahim Hayri', 'Anesthesia & Surgery', 'Red', '86903005'),
    ('Morphine', 'Galen', 'Pain relief', 'Red', '86903006'),
    ('OxyContin', 'Mundipharma', 'Pain relief', 'Red', '86903007'),
    ('Actiq', 'Teva', 'Pain relief', 'Red', '86903008'),
    ('Fentanyl', 'Hameln', 'Anesthesia & Surgery', 'Red', '86903009'),
    ('Pethidine', 'Polifarma', 'Anesthesia & Surgery', 'Red', '86903010'),

    # Orange 4
    ('Advate', 'Takeda', 'Blood Products', 'Orange', '86904001'),
    ('Benefix', 'Pfizer', 'Blood Products', 'Orange', '86904002'),
    ('Feiba', 'Takeda', 'Blood Products', 'Orange', '86904003'),
    ('NovoSeven', 'Novo Nordisk', 'Blood Products', 'Orange', '86904004'),
    ('Haemate P', 'CSL Behring', 'Blood Products', 'Orange', '86904005'),

    # Purple 5
    ('Human Albumin', 'Behring', 'Blood Products', 'Purple', '86905001'),
    ('Privigen', 'CSL Behring', 'Blood Products', 'Purple', '86905002'),
    ('Octagam', 'Octapharma', 'Blood Products', 'Purple', '86905003'),
    ('Beriglobin', 'CSL Behring', 'Blood Products', 'Purple', '86905004'),
    ('Anti-D', 'Kedrion', 'Blood Products', 'Purple', '86905005'),
]

out = open('C:/Users/iremy/Desktop/Pharmacy Management System/src/main/java/com/pharmacy/dao/DatabaseSeeder_part.txt', 'w', encoding='utf-8')

for name, brand, cat, pres, barcode in drugs:
    c_id = cat_map[cat]
    b_id = brand_map[brand]
    p_id = pres_map[pres]
    out.write(f'        dao.save(drug("{barcode}", "{name} 50mg", "Tablet/Vial", "15.00", "25.00", 100, {c_id}L, {b_id}, {p_id}));\\n')

out.close()
