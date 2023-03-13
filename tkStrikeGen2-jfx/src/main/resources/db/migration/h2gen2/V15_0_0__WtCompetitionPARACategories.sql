-- AFEEGIM CATEGORIES

INSERT INTO TKS_SUBCATEGORY (ID,version,NAME)
VALUES
    ('14',0,'K 41'),
    ('15',0,'K 44');

INSERT INTO TKS_CATEGORY (ID,version,GENDER,NAME,SUBCATEGORY_ID,BODY_LEVEL,HEAD_LEVEL)
VALUES
  -- MALE K41
  ('095',0,'MALE','M -58KG','14',19,5),
  ('096',0,'MALE','M -63KG','14',20,5),
  ('097',0,'MALE','M -70KG','14',21,5),
  ('098',0,'MALE','M -80KG','14',22,5),
  ('099',0,'MALE','M +80KG','14',24,5),
  -- FEMALE K41
  ('100',0,'FEMALE','F -47KG','14',15,5),
  ('101',0,'FEMALE','F -52KG','14',16,5),
  ('102',0,'FEMALE','F -57KG','14',17,5),
  ('103',0,'FEMALE','F -65KG','14',18,5),
  ('104',0,'FEMALE','F +65KG','14',20,5),
  -- MALE K44
  ('105',0,'MALE','M -58KG','15',19,5),
  ('106',0,'MALE','M -63KG','15',20,5),
  ('107',0,'MALE','M -70KG','15',21,5),
  ('108',0,'MALE','M -80KG','15',22,5),
  ('109',0,'MALE','M +80KG','15',24,5),
  -- FEMALE K414
  ('110',0,'FEMALE','F -47KG','15',15,5),
  ('111',0,'FEMALE','F -52KG','15',16,5),
  ('112',0,'FEMALE','F -57KG','15',17,5),
  ('113',0,'FEMALE','F -65KG','15',18,5),
  ('114',0,'FEMALE','F +65KG','15',20,5)
  ;