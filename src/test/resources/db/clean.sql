DELETE FROM pf.milestone;
SELECT FROM setval('pf.milestone_id_seq', 1, false);
DELETE FROM pf.notification;
SELECT setval('pf.notification_id_seq', 1, false);
DELETE FROM pf.project_journal;
SELECT setval('pf.project_journal_id_seq', 1, false);
DELETE FROM pf.project_comment;
SELECT setval('pf.project_comment_id_seq', 1, false);
DELETE FROM pf.project;
SELECT setval('pf.project_id_seq', 1, false);
DELETE FROM pf.direction;
SELECT setval('pf.direction_id_seq', 1, false);
DELETE FROM pf.employee;
SELECT setval('pf.employee_id_seq', 1, false);
