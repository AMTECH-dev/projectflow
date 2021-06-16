insert into pf.employee(id, name, email, phone, position, is_fired)
values  (11, 'Иван Иванов', 'ivan@gmail.com', '111111111', 'DIRECTOR', false),
        (22, 'Пётр Петров', 'petr@gmail.com', '222222222', 'DIRECTION_LEAD', false),
        (33, 'Сидор Сидоров', 'sidor@gmail.com', '333333333', 'PROJECT_LEAD', false),
        (44, 'Фёдор Фёдоров', 'fedor@gmail.com', '444444444', 'DIRECTION_LEAD', false),
        (55, 'Николай Николаев', 'nik@gmail.com', '555555555', 'PROJECT_LEAD', false),
        (66, 'Евгения Евгеньева', 'eva@gmail.com', '666666666', 'PROJECT_LEAD', false);

insert into pf.direction(id, lead_id, name)
values (11, 22, 'hr'),
       (22, 44, 'sales');

insert into pf.project(id, name, project_lead_id, direction_id, description, create_date, status)
values (11, 'First', 33, 11, '', current_timestamp, 'UNAPPROVED'),
       (22, 'Second', 33, 11, '', '2021-02-12 14:11:31+003', 'ON_PL_PLANNING'),
       (33, 'Third', 55, 22, '', current_timestamp, 'ON_DL_APPROVING'),
       (44, 'Fourth', 55, 22, '', '2021-05-11 10:21:23+003', 'ON_PL_PLANNING'),
       (55, 'Fifth', 66, 22, '', current_timestamp, 'ON_DL_APPROVING');

insert into pf.milestone(id, project_id, name, description, planned_start_date, planned_finish_date,
                         fact_start_date, fact_finish_date, progress_percent)
values (11, 11, 'Первая', '', '2021-06-10T10:15:00Z', '2021-06-11T14:01:41Z', null, null, 90),
       (22, 11, 'Вторая', '', '2021-06-10T10:15:00Z', '2021-06-11T14:01:41Z', null, null, 80),
       (33, 11, 'Третья', '', '2021-06-10T10:15:00Z', '2021-06-11T14:01:41Z', null, null, 70),
       (44, 11, 'Четвертая', '', '2021-06-10T10:15:00Z', '2021-06-11T14:01:41Z', null, null, 60),
       (55, 11, 'Пятая', '', '2021-06-10T10:15:00Z', '2021-06-14T19:48:21Z', null, null, 50),
       (66, 22, 'Шестая', '', '2021-06-10T10:15:00Z', '2021-06-11T14:01:41Z', null, null, 40),
       (77, 33, 'Седьмая', '', '2021-06-10T10:15:00Z', '2021-06-11T14:01:41Z', null, null, 30),
       (88, 44, 'Восьмая', '', '2021-06-10T10:15:00Z', '2021-06-11T14:01:41Z', null, null, 20),
       (99, 55, 'Девятая', '', '2021-06-10T10:15:00Z', '2021-06-11T14:01:41Z', null, null, 10);
