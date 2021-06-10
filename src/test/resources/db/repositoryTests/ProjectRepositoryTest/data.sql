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
       ('DROP DATABASE', 4, 1, '', '2021-06-09T11:49:03.839234Z', 'ON_PL_PLANNING'),
       ('Projectflow ', 4, 1, '', current_timestamp, 'ON_DL_APPROVING'),
       ('SUPER PROJECT ', 5, 10, null, '2010-02-09T11:49:03.839234Z', 'ON_DL_APPROVING'),
       ('UPDATE RUSSIAN STREET', 5, 1, null, current_timestamp, 'ON_DL_APPROVING');
