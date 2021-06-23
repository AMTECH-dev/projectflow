insert into pf.employee(id, name, email, phone, position, is_fired)
values  (11, 'Иван Иванов', 'ivan@gmail.com', '111111111', 'DIRECTOR', false),
        (22, 'Пётр Петров', 'petr@gmail.com', '222222222', 'DIRECTION_LEAD', false),
        (33, 'Сидор Сидоров', 'sidor@gmail.com', '333333333', 'PROJECT_LEAD', false),
        (44, 'Фёдор Фёдоров', 'fedor@gmail.com', '444444444', 'DIRECTION_LEAD', false),
        (55, 'Николай Николаев', 'nik@gmail.com', '555555555', 'PROJECT_LEAD', false),
        (66, 'Евгения Евгеньева', 'eva@gmail.com', '666666666', 'PROJECT_LEAD', false);

insert into pf.direction(id, lead_id, name)
values (100, 11, 'Основной Direction'),
       (200, 22, 'sales'),
       (300, 33, 'IT'),
       (400, 44, 'HR'),
       (500, 55, 'Delivery'),
       (600, 66, 'Calculation'),
       (700, 66, 'Gas'),
       (800, 66, 'Oil'),
       (900, 66, 'Water'),
       (1000, 66, 'Rubbish');
