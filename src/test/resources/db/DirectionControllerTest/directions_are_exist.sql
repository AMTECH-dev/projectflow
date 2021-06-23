insert into pf.employee(id, name, email, phone, position, is_fired)
values  (11, 'Иван Иванов', 'ivan@gmail.com', '111111111', 'DIRECTOR', false),
        (22, 'Пётр Петров', 'petr@gmail.com', '222222222', 'DIRECTION_LEAD', false),
        (33, 'Сидор Сидоров', 'sidor@gmail.com', '333333333', 'PROJECT_LEAD', false),
        (44, 'Фёдор Фёдоров', 'fedor@gmail.com', '444444444', 'DIRECTION_LEAD', false),
        (55, 'Николай Николаев', 'nik@gmail.com', '555555555', 'PROJECT_LEAD', false),
        (66, 'Евгения Евгеньева', 'eva@gmail.com', '666666666', 'PROJECT_LEAD', false);

insert into pf.direction(id, lead_id, name)
values (100, 22, 'Основной Direction'),
       (200, 44, 'sales'),
       (300, 66, 'IT');