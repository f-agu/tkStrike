-- Insert default EXTERNAL_CONFIG
INSERT INTO TKS_EXTERNAL_CONFIG (ID, VERSION,
                                VM_URL,VM_RING_NUMBER,
                                EXT_SCOREBOARD_BLUEONLEFT, EXT_SCOREBOARD_RESOLUTION,
                                RTB_IP, RTB_PORT, RTB_RING_NUM,
                                MATCH_LOG_OUTPUT_DIRECTORY,
                                WT_OVR_URL, WT_OVR_X_API_KEY, WT_OVR_MAT,
                                WT_OVR_UDP_IP, WT_OVR_UDP_LISTEN_PORT, WT_OVR_UDP_WRITE_PORT)
VALUES ('1', 1,
        null, null,
        true, 'HD',
        null, 0, null,
        './matchLogExport',
        null, null, null,
        null, null, null);

-- Insert default to RULES
INSERT INTO TKS_RULES (ID, VERSION,
                        BODY_POINTS, HEAD_POINTS, PUNCH_POINTS,
                        BODY_TECH_POINTS, HEAD_TECH_POINTS,
                        OVERTIME_POINTS, CELLING_SCORE, DIFFERENCIAL_SCORE,
                        NEAR_MISS_LEVEL, ROUNDS, ROUND_TIME, KYESHI_TIME, REST_TIME, GOLDENPOINT_ENABLED, GOLDENPOINT_TIME,
                        PARA_SPINNING_KICK_POINTS, PARA_TURNING_KICK_POINTS,
                        ALL_MATCH_PARA, OVERTIME_PENALTIES, MATCH_VICTORY_CRITERIA, GAMJEOM_SHOW_POINTS_ON_GOLDENPOINT)
VALUES ('1', 1, 2, 3, 1, 2, 2, 2, 99, 20, 7, 3, '2:00', '01:00', '01:00', true, '01:00', 2, 1, false, 2, 'BY_POINTS', false);

-- Insert default Network Config
INSERT INTO TKS_NETWORK_CONFIG (ID, VERSION, NETWORK_WAS_STARTED, CHANNEL,
                                N_JUDGES, JD_1_NODE_ID, JD_2_NODE_ID, JD_3_NODE_ID,
                                HEAD_TP_ENABLED,
                                G1_HS_ENABLED, G1_BS_ENABLED, G1_BB_NODE_ID, G1_HB_NODE_ID, G1_BR_NODE_ID, G1_HR_NODE_ID,
                                GROUP2_ENABLED, G2_HS_ENABLED, G2_BS_ENABLED,
                                G2_BB_NODE_ID, G2_HB_NODE_ID, G2_BR_NODE_ID, G2_HR_NODE_ID,
                                N_GROUPS,
                                G3_HS_ENABLED, G3_BS_ENABLED, G3_BB_NODE_ID, G3_HB_NODE_ID, G3_BR_NODE_ID, G3_HR_NODE_ID,
                                G4_HS_ENABLED, G4_BS_ENABLED, G4_BB_NODE_ID, G4_HB_NODE_ID, G4_BR_NODE_ID, G4_HR_NODE_ID,
                                G5_HS_ENABLED, G5_BS_ENABLED, G5_BB_NODE_ID, G5_HB_NODE_ID, G5_BR_NODE_ID, G5_HR_NODE_ID,
                                G6_HS_ENABLED, G6_BS_ENABLED, G6_BB_NODE_ID, G6_HB_NODE_ID, G6_BR_NODE_ID, G6_HR_NODE_ID)
VALUES ('1', 1, false, 14,
        3, '', '', '',null,
        true, true, '', '', '', '', false, true, true, null, null, null, null, 1, true, true, null, null, null, null, true, true, null, null, null, null, true, true, null, null, null, null, true, true, null, null, null, null);

INSERT INTO TKS_PHASE (ID,version,NAME,ABBREVIATION)
VALUES
  ('01',0,'Round of 128','R128'),
  ('02',0,'Round of 64','R64'),
  ('03',0,'Round of 32','R32'),
  ('04',0,'Round of 16','R16'),
  ('05',0,'Quarterfinals','QF'),
  ('06',0,'Semifinals','SF'),
  ('07',0,'Bronze medal contest','BMC'),
  ('08',0,'Final','F'),
  ('11',0,'Repechage','RP'),
  ('12',0,'Other','OT');

-- 1/2 Final - SENIORS
INSERT INTO TKS_DIFFERENTIAL_SCORE_DEF (ID,VERSION,PHASE_ID,SUBCATEGORY_ID,THE_VALUE)
VALUES ('1',0,'06','4',99);

-- 1/2 Final - OLYMPIC CATEGORY
INSERT INTO TKS_DIFFERENTIAL_SCORE_DEF (ID,VERSION,PHASE_ID,SUBCATEGORY_ID,THE_VALUE)
VALUES ('2',0,'06','5',99);

-- Final - SENIORS
INSERT INTO TKS_DIFFERENTIAL_SCORE_DEF (ID,VERSION,PHASE_ID,SUBCATEGORY_ID,THE_VALUE)
VALUES ('3',0,'08','4',99);

-- Final - OLYMPIC CATEGORY
INSERT INTO TKS_DIFFERENTIAL_SCORE_DEF (ID,VERSION,PHASE_ID,SUBCATEGORY_ID,THE_VALUE)
VALUES ('4',0,'08','5',99);