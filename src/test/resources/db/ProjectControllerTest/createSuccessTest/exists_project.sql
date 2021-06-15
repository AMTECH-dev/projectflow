

insert into pf.employee(id, name, email, phone, position, is_fired) values
(1, 'Igor', 'i@i.ru', '79888888888', 'DIRECTOR',false),
(2, 'Oxa', 'o@i.ru', '79888888800', 'DIRECTOR',false);

insert into pf.direction (id, lead_id, name) values
(1,1,'Nat'),
(2,1,'Vasya');

-- insert into pf.project (id, name, project_lead_id, direction_id, description, create_date, status) values
-- (1, 'One', 1 , 1, 'message 1', '2021-06-09T11:49:03.839234Z', 'UNAPPROVED');
-- (2, 'Two', 2 , 2, 'message 2', '2021-08-09T11:49:03.839234Z', 'UNAPPROVED'),
-- (3, 'Three', 2 , 2, 'message ', '2021-07-09T11:49:03.839234Z', 'ON_PL_PLANNING');