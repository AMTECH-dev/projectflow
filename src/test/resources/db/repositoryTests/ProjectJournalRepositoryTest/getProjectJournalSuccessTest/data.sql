insert into pf.employee(id, name, email, phone, position, is_fired)
values (1, 'ОЛЕГ', 'my-email@gmail.com', '999999999', 'DIRECTOR', false),
       (2, 'ВАНА', 'my-email@gmail.com', '999999999', 'DIRECTION_LEAD', false),
       (4, 'ФЕДОР', 'my-email@gmail.com', '999999999', 'PROJECT_LEAD', false),
       (10, 'Катя', 'my-email@gmail.com', '999999999', 'DIRECTION_LEAD', false),
       (5, 'Йосип', 'my-email@gmail.com', '999999999', 'PROJECT_LEAD', false);

insert into pf.direction(id, lead_id, name)
values (1, 2, 'dev'),
       (10, 10, 'test');

insert into pf.project(name, project_lead_id, direction_id, description, create_date, status)
values ('Hack NASA using HTML', 4, 10, '', current_timestamp, 'UNAPPROVED'),
       ('DROP DATABASE ', 4, 1, '', '2022-07-02 15:31:59+003', 'ON_PL_PLANNING'),
       ('Projectflow ', 4, 1, '', current_timestamp, 'ON_DL_APPROVING'),
       ('SUPER PROJECT ', 5, 10, '', '2007-05-20 08:30:42+001', 'ON_PL_PLANNING'),
       ('UPDATE RUSSIAN STREET', 5, 1, '', current_timestamp, 'ON_DL_APPROVING');

insert into pf.project_journal(project_id, login, update_date, current_state)
values (2, 'fedor', current_timestamp, '{"a": "dwq", "v": "updated", "field": {"a": "dwq", "v": "updated", "field": "updated"}}'),
       (3, 'fedor', current_timestamp, '{"a": "dwq", "v": "updated", "field": "updated"}'),
       (4, 'fedor', current_timestamp, '{"a": "dwq", "v": "updated", "field": "updated"}'),
       (2, 'fedor', current_timestamp, '{"a": "dwq", "v": "updated", "field": "updated"}'),
       (1, 'fedor', current_timestamp, '{"a": "dwq", "v": "updated", "field": "updated"}'),
       (3, 'fedor', current_timestamp, '{"a": "dwq", "v": "updated", "field": "updated"}'),
       (5, 'OLEG', current_timestamp, '{"ww": "dewq", "field1": "fieldname"}'),
       (4, 'OLEG', current_timestamp, '{"ww": "dewq", "field1": "fieldname"}'),
       (4, 'OLEG', current_timestamp, '{"ww": "dewq", "field1": "fieldname"}'),
       (5, 'OLEG', current_timestamp, '{"ww": "dewq", "field1": "fieldname"}'),
       (1, 'fedor', current_timestamp, '{"ss": "ewq", "project": "i"}'),
       (5, 'OLEG', current_timestamp, '{"eqw": "qwwqeq", "sda": "ewqq"}'),
       (5, 'OLEG', current_timestamp, '{"eqw": "qwwqeq", "sda": "ewqq"}'),
       (5, 'OLEG', current_timestamp, '{"eqw": "qwwqeq", "sda": "ewqq"}');

