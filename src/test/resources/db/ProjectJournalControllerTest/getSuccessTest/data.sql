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
values (2, 'oleg', '2002-02-09T11:49:03', '{"a": "dwq", "v": "updated", "field": {"a": "dwq", "v": "updated", "field": "updated"}}'),
       (3, 'sim', '2013-02-02T12:29:03', '{"a": "dwq", "v": "updated", "field": "updated"}'),
       (2, 'vika', '2017-02-09T11:49:03', '{"a": "dwq", "v3": "some update", "field": "updated"}'),
       (3, 'zina', '2015-02-09T11:49:03', '{"a": "UPDATe", "v2": "WOW", "field": "baby"}'),
       (1, 'anna', '2020-03-09T11:49:03', '{"a": "dwq", "v1": "updated", "field": "create project"}'),
       (2, 'olina', '2007-03-09T11:49:03', '{"a": "dwq", "some": {"message": "super update"}, "field": "wow nwe update"}'),
       (2, 'anna', '2021-02-28T02:50:02', '{"a": "dwq", "v1": "updated", "field": "create project"}');

