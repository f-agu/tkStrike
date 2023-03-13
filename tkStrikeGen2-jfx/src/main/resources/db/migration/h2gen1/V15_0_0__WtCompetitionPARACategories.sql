-- AFEEGIM CATEGORIES

INSERT INTO TKS_SUBCATEGORY (ID,version,NAME)
VALUES
    ('14',0,'K 41'),
    ('15',0,'K 44');

INSERT INTO TKS_CATEGORY (ID,version,GENDER,NAME,SUBCATEGORY_ID,BODY_LEVEL,HEAD_LEVEL)
VALUES
  -- MALE K41
  ('140',0,'MALE','M -58KG','14',19,5),
  ('141',0,'MALE','M -63KG','14',20,5),
  ('142',0,'MALE','M -70KG','14',21,5),
  ('143',0,'MALE','M -80KG','14',22,5),
  ('144',0,'MALE','M +80KG','14',24,5),
  -- FEMALE K41
  ('145',0,'FEMALE','F -47KG','14',15,5),
  ('146',0,'FEMALE','F -52KG','14',16,5),
  ('147',0,'FEMALE','F -57KG','14',17,5),
  ('148',0,'FEMALE','F -65KG','14',18,5),
  ('149',0,'FEMALE','F +65KG','14',20,5),
  -- MALE K44
  ('150',0,'MALE','M -58KG','15',19,5),
  ('151',0,'MALE','M -63KG','15',20,5),
  ('152',0,'MALE','M -70KG','15',21,5),
  ('153',0,'MALE','M -80KG','15',22,5),
  ('154',0,'MALE','M +80KG','15',24,5),
  -- FEMALE K414
  ('155',0,'FEMALE','F -47KG','15',15,5),
  ('156',0,'FEMALE','F -52KG','15',16,5),
  ('157',0,'FEMALE','F -57KG','15',17,5),
  ('158',0,'FEMALE','F -65KG','15',18,5),
  ('159',0,'FEMALE','F +65KG','15',20,5)
  ;