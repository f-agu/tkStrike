ALTER TABLE TKS_RULES
    ADD COLUMN POINT_GAP_ALL_ROUNDS BOOLEAN DEFAULT FALSE;

UPDATE TKS_RULES SET POINT_GAP_ALL_ROUNDS = 0;

