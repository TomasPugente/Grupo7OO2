# Grupo7OO2
Script SQL
-- =====================================================================
-- Datos de prueba - Epicentro Gourmet (Grupo 7 OO2)
-- 5 filas de costos (una por festival), 3 usuarios (admin, responsable, empleado),
-- 1 responsable y 1 empleado.
-- Contraseña de los 3 usuarios: 1234

-- =====================================================================
-- drop database festival_gourmet; --
-- create database festival_gourmet; --
USE festival_gourmet;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE detalle_pedido;
TRUNCATE TABLE pedido;
TRUNCATE TABLE plato;
TRUNCATE TABLE staff;
TRUNCATE TABLE responsable;
TRUNCATE TABLE costos;
TRUNCATE TABLE unidad_de_venta;
TRUNCATE TABLE festival;
TRUNCATE TABLE user_role;
TRUNCATE TABLE `user`;
SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------
-- 5 festivales
-- ---------------------------------------------------------------------
INSERT INTO festival (id_festival, activo, created_at, updated_at, fecha_inicio, fecha_fin, nombre, temporada) VALUES
(1, 1, NOW(6), NOW(6), '2026-12-01', '2026-12-15', 'Festival del Chocolate', 'Verano 2026'),
(2, 1, NOW(6), NOW(6), '2026-04-10', '2026-04-25', 'Feria de Sabores del Mar', 'Otoño 2026'),
(3, 1, NOW(6), NOW(6), '2027-03-21', '2027-03-28', 'Festival Otoño Criollo', 'Otoño'),
(4, 1, NOW(6), NOW(6), '2027-06-21', '2027-06-28', 'Festival Invierno Cocinado', 'Invierno'),
(5, 1, NOW(6), NOW(6), '2027-09-21', '2027-09-30', 'Gran Festival Gourmet Anual', 'Anual');

-- ---------------------------------------------------------------------
-- 25 unidades de venta (5 por festival). Impares: Food Truck, pares: Puesto Desarmable.
-- El código tiene 10 caracteres (FT/PD + número).
-- ---------------------------------------------------------------------
INSERT INTO unidad_de_venta (dtype, id_unidad_de_venta, activo, codigo, created_at, updated_at, nombre_comercial, recaudacion, superficie, patente, requiere_electricidad, festival_id) VALUES
('FoodTruck', 1, 1, 'FT00000001', NOW(6), NOW(6), 'Sabor Criollo', 366250, 12.5, 'AB101CD', 1, 1),
('FoodTruck', 3, 1, 'FT00000003', NOW(6), NOW(6), 'Pasta Nonna', 858750, 21.5, 'AB103CD', 0, 1),
('FoodTruck', 5, 1, 'FT00000005', NOW(6), NOW(6), 'Dulce Tentación', 476250, 8.0, 'AB105CD', 1, 1),
('FoodTruck', 7, 1, 'FT00000007', NOW(6), NOW(6), 'El Buen Bocado', 968750, 17.0, 'AB107CD', 1, 2),
('FoodTruck', 9, 1, 'FT00000009', NOW(6), NOW(6), 'Casa Vegana', 586250, 26.0, 'AB109CD', 0, 2),
('FoodTruck', 11, 1, 'FT00000011', NOW(6), NOW(6), 'Wok Express', 203750, 12.5, 'AB111CD', 1, 3),
('FoodTruck', 13, 1, 'FT00000013', NOW(6), NOW(6), 'La Pizzería', 696250, 21.5, 'AB113CD', 1, 3),
('FoodTruck', 15, 1, 'FT00000015', NOW(6), NOW(6), 'Sushi Pampa', 313750, 8.0, 'AB115CD', 0, 3),
('FoodTruck', 17, 1, 'FT00000017', NOW(6), NOW(6), 'Postres del Valle', 806250, 17.0, 'AB117CD', 1, 4),
('FoodTruck', 19, 1, 'FT00000019', NOW(6), NOW(6), 'Milanesa Master', 423750, 26.0, 'AB119CD', 1, 4),
('FoodTruck', 21, 1, 'FT00000021', NOW(6), NOW(6), 'Pollo Crocante', 916250, 12.5, 'AB121CD', 0, 5),
('FoodTruck', 23, 1, 'FT00000023', NOW(6), NOW(6), 'Panes y Más', 533750, 21.5, 'AB123CD', 1, 5),
('FoodTruck', 25, 1, 'FT00000025', NOW(6), NOW(6), 'Sabores del Norte', 151250, 8.0, 'AB125CD', 1, 5);

INSERT INTO unidad_de_venta (dtype, id_unidad_de_venta, activo, codigo, created_at, updated_at, nombre_comercial, recaudacion, superficie, cantidad_carpas, tiempo_montaje, festival_id) VALUES
('PuestoDesarmable', 2, 1, 'PD00000002', NOW(6), NOW(6), 'La Parrilla Dorada', 612500, 17.0, 4, 34, 1),
('PuestoDesarmable', 4, 1, 'PD00000004', NOW(6), NOW(6), 'Tacos del Sur', 230000, 26.0, 2, 38, 1),
('PuestoDesarmable', 6, 1, 'PD00000006', NOW(6), NOW(6), 'Mar y Tierra', 722500, 12.5, 4, 42, 2),
('PuestoDesarmable', 8, 1, 'PD00000008', NOW(6), NOW(6), 'Fuego Lento', 340000, 21.5, 2, 46, 2),
('PuestoDesarmable', 10, 1, 'PD00000010', NOW(6), NOW(6), 'Empanadas Don Pepe', 832500, 8.0, 4, 50, 2),
('PuestoDesarmable', 12, 1, 'PD00000012', NOW(6), NOW(6), 'Burger Lab', 450000, 17.0, 2, 54, 3),
('PuestoDesarmable', 14, 1, 'PD00000014', NOW(6), NOW(6), 'Café Gourmet', 942500, 26.0, 4, 58, 3),
('PuestoDesarmable', 16, 1, 'PD00000016', NOW(6), NOW(6), 'Choripán Real', 560000, 12.5, 2, 62, 4),
('PuestoDesarmable', 18, 1, 'PD00000018', NOW(6), NOW(6), 'Cocina Andina', 177500, 21.5, 4, 66, 4),
('PuestoDesarmable', 20, 1, 'PD00000020', NOW(6), NOW(6), 'Helados Artesanales', 670000, 8.0, 2, 70, 4),
('PuestoDesarmable', 22, 1, 'PD00000022', NOW(6), NOW(6), 'Arroces del Mundo', 287500, 17.0, 4, 74, 5),
('PuestoDesarmable', 24, 1, 'PD00000024', NOW(6), NOW(6), 'Gaucho Grill', 780000, 26.0, 2, 78, 5);

-- ---------------------------------------------------------------------
-- Costos de cada festival (5 filas, una por festival). De acá salen los valores del canon
-- ($500 por m2, $2000 de electricidad, $10 por minuto de montaje) y de los sueldos.
-- ---------------------------------------------------------------------
INSERT INTO costos (id_costos, costo_por_superficie, costo_por_montaje, plus_por_electricidad, sueldo_base, monto_anio_antiguedad, plus_de_cocinero, plus_de_cocinero_ayudante, plus_de_lava_platos, festival_id, created_at, updated_at) VALUES
(1, 500, 10, 2000, 100000, 5000, 150000, 100000, 50000, 1, NOW(6), NOW(6)),
(2, 500, 10, 2000, 100000, 5000, 150000, 100000, 50000, 2, NOW(6), NOW(6)),
(3, 500, 10, 2000, 100000, 5000, 150000, 100000, 50000, 3, NOW(6), NOW(6)),
(4, 500, 10, 2000, 100000, 5000, 150000, 100000, 50000, 4, NOW(6), NOW(6)),
(5, 500, 10, 2000, 100000, 5000, 150000, 100000, 50000, 5, NOW(6), NOW(6));

-- ---------------------------------------------------------------------
-- 125 platos (5 por unidad). Los platos 5, 10 y 15 están eliminados lógicamente
-- (activo = 0) para probar que no aparecen en el listado ni en el filtro.
-- ---------------------------------------------------------------------
INSERT INTO plato (id, activo, nombre, precio_venta, costo_produccion, unidad_de_venta_id) VALUES
(1, 1, 'Papas rústicas', 3300, 1100, 1),
(2, 1, 'Ensalada fresca', 3900, 1300, 1),
(3, 1, 'Pizza de muzzarella', 6600, 2400, 1),
(4, 1, 'Choripán', 4400, 1600, 1),
(5, 0, 'Tacos de pollo', 6000, 2100, 1),
(6, 1, 'Empanada de carne', 2100, 650, 2),
(7, 1, 'Limonada casera', 2500, 500, 2),
(8, 1, 'Milanesa con puré', 7500, 2900, 2),
(9, 1, 'Flan con dulce de leche', 3800, 1000, 2),
(10, 0, 'Hamburguesa clásica', 5800, 2200, 2),
(11, 1, 'Papas rústicas', 3500, 1100, 3),
(12, 1, 'Ensalada fresca', 4100, 1300, 3),
(13, 1, 'Pizza de muzzarella', 6800, 2400, 3),
(14, 1, 'Choripán', 4600, 1600, 3),
(15, 0, 'Tacos de pollo', 6200, 2100, 3),
(16, 1, 'Empanada de carne', 2300, 650, 4),
(17, 1, 'Limonada casera', 2700, 500, 4),
(18, 1, 'Milanesa con puré', 7700, 2900, 4),
(19, 1, 'Flan con dulce de leche', 4000, 1000, 4),
(20, 1, 'Hamburguesa clásica', 6000, 2200, 4),
(21, 1, 'Papas rústicas', 3700, 1100, 5),
(22, 1, 'Ensalada fresca', 4300, 1300, 5),
(23, 1, 'Pizza de muzzarella', 7000, 2400, 5),
(24, 1, 'Choripán', 4800, 1600, 5),
(25, 1, 'Tacos de pollo', 6400, 2100, 5),
(26, 1, 'Empanada de carne', 2000, 650, 6),
(27, 1, 'Limonada casera', 2400, 500, 6),
(28, 1, 'Milanesa con puré', 7400, 2900, 6),
(29, 1, 'Flan con dulce de leche', 3700, 1000, 6),
(30, 1, 'Hamburguesa clásica', 5700, 2200, 6),
(31, 1, 'Papas rústicas', 3400, 1100, 7),
(32, 1, 'Ensalada fresca', 4000, 1300, 7),
(33, 1, 'Pizza de muzzarella', 6700, 2400, 7),
(34, 1, 'Choripán', 4500, 1600, 7),
(35, 1, 'Tacos de pollo', 6100, 2100, 7),
(36, 1, 'Empanada de carne', 2200, 650, 8),
(37, 1, 'Limonada casera', 2600, 500, 8),
(38, 1, 'Milanesa con puré', 7600, 2900, 8),
(39, 1, 'Flan con dulce de leche', 3900, 1000, 8),
(40, 1, 'Hamburguesa clásica', 5900, 2200, 8),
(41, 1, 'Papas rústicas', 3600, 1100, 9),
(42, 1, 'Ensalada fresca', 4200, 1300, 9),
(43, 1, 'Pizza de muzzarella', 6900, 2400, 9),
(44, 1, 'Choripán', 4700, 1600, 9),
(45, 1, 'Tacos de pollo', 6300, 2100, 9),
(46, 1, 'Empanada de carne', 2400, 650, 10),
(47, 1, 'Limonada casera', 2800, 500, 10),
(48, 1, 'Milanesa con puré', 7800, 2900, 10),
(49, 1, 'Flan con dulce de leche', 4100, 1000, 10),
(50, 1, 'Hamburguesa clásica', 6100, 2200, 10),
(51, 1, 'Papas rústicas', 3300, 1100, 11),
(52, 1, 'Ensalada fresca', 3900, 1300, 11),
(53, 1, 'Pizza de muzzarella', 6600, 2400, 11),
(54, 1, 'Choripán', 4400, 1600, 11),
(55, 1, 'Tacos de pollo', 6000, 2100, 11),
(56, 1, 'Empanada de carne', 2100, 650, 12),
(57, 1, 'Limonada casera', 2500, 500, 12),
(58, 1, 'Milanesa con puré', 7500, 2900, 12),
(59, 1, 'Flan con dulce de leche', 3800, 1000, 12),
(60, 1, 'Hamburguesa clásica', 5800, 2200, 12),
(61, 1, 'Papas rústicas', 3500, 1100, 13),
(62, 1, 'Ensalada fresca', 4100, 1300, 13),
(63, 1, 'Pizza de muzzarella', 6800, 2400, 13),
(64, 1, 'Choripán', 4600, 1600, 13),
(65, 1, 'Tacos de pollo', 6200, 2100, 13),
(66, 1, 'Empanada de carne', 2300, 650, 14),
(67, 1, 'Limonada casera', 2700, 500, 14),
(68, 1, 'Milanesa con puré', 7700, 2900, 14),
(69, 1, 'Flan con dulce de leche', 4000, 1000, 14),
(70, 1, 'Hamburguesa clásica', 6000, 2200, 14),
(71, 1, 'Papas rústicas', 3700, 1100, 15),
(72, 1, 'Ensalada fresca', 4300, 1300, 15),
(73, 1, 'Pizza de muzzarella', 7000, 2400, 15),
(74, 1, 'Choripán', 4800, 1600, 15),
(75, 1, 'Tacos de pollo', 6400, 2100, 15),
(76, 1, 'Empanada de carne', 2000, 650, 16),
(77, 1, 'Limonada casera', 2400, 500, 16),
(78, 1, 'Milanesa con puré', 7400, 2900, 16),
(79, 1, 'Flan con dulce de leche', 3700, 1000, 16),
(80, 1, 'Hamburguesa clásica', 5700, 2200, 16),
(81, 1, 'Papas rústicas', 3400, 1100, 17),
(82, 1, 'Ensalada fresca', 4000, 1300, 17),
(83, 1, 'Pizza de muzzarella', 6700, 2400, 17),
(84, 1, 'Choripán', 4500, 1600, 17),
(85, 1, 'Tacos de pollo', 6100, 2100, 17),
(86, 1, 'Empanada de carne', 2200, 650, 18),
(87, 1, 'Limonada casera', 2600, 500, 18),
(88, 1, 'Milanesa con puré', 7600, 2900, 18),
(89, 1, 'Flan con dulce de leche', 3900, 1000, 18),
(90, 1, 'Hamburguesa clásica', 5900, 2200, 18),
(91, 1, 'Papas rústicas', 3600, 1100, 19),
(92, 1, 'Ensalada fresca', 4200, 1300, 19),
(93, 1, 'Pizza de muzzarella', 6900, 2400, 19),
(94, 1, 'Choripán', 4700, 1600, 19),
(95, 1, 'Tacos de pollo', 6300, 2100, 19),
(96, 1, 'Empanada de carne', 2400, 650, 20),
(97, 1, 'Limonada casera', 2800, 500, 20),
(98, 1, 'Milanesa con puré', 7800, 2900, 20),
(99, 1, 'Flan con dulce de leche', 4100, 1000, 20),
(100, 1, 'Hamburguesa clásica', 6100, 2200, 20),
(101, 1, 'Papas rústicas', 3300, 1100, 21),
(102, 1, 'Ensalada fresca', 3900, 1300, 21),
(103, 1, 'Pizza de muzzarella', 6600, 2400, 21),
(104, 1, 'Choripán', 4400, 1600, 21),
(105, 1, 'Tacos de pollo', 6000, 2100, 21),
(106, 1, 'Empanada de carne', 2100, 650, 22),
(107, 1, 'Limonada casera', 2500, 500, 22),
(108, 1, 'Milanesa con puré', 7500, 2900, 22),
(109, 1, 'Flan con dulce de leche', 3800, 1000, 22),
(110, 1, 'Hamburguesa clásica', 5800, 2200, 22),
(111, 1, 'Papas rústicas', 3500, 1100, 23),
(112, 1, 'Ensalada fresca', 4100, 1300, 23),
(113, 1, 'Pizza de muzzarella', 6800, 2400, 23),
(114, 1, 'Choripán', 4600, 1600, 23),
(115, 1, 'Tacos de pollo', 6200, 2100, 23),
(116, 1, 'Empanada de carne', 2300, 650, 24),
(117, 1, 'Limonada casera', 2700, 500, 24),
(118, 1, 'Milanesa con puré', 7700, 2900, 24),
(119, 1, 'Flan con dulce de leche', 4000, 1000, 24),
(120, 1, 'Hamburguesa clásica', 6000, 2200, 24),
(121, 1, 'Papas rústicas', 3700, 1100, 25),
(122, 1, 'Ensalada fresca', 4300, 1300, 25),
(123, 1, 'Pizza de muzzarella', 7000, 2400, 25),
(124, 1, 'Choripán', 4800, 1600, 25),
(125, 1, 'Tacos de pollo', 6400, 2100, 25);

-- ---------------------------------------------------------------------
-- 3 usuarios (contraseña: 1234) y sus roles
-- ---------------------------------------------------------------------
INSERT INTO `user` (id, created_at, updated_at, enabled, username, password) VALUES
(1, NOW(6), NOW(6), 1, 'admin@festival.com', '$2a$10$SRRTss/yqID0jcv/oHU5oeAzAPA/7DV9e69FrP/fGJZAamndOvTjW'),
(2, NOW(6), NOW(6), 1, 'responsable@festival.com', '$2a$10$SRRTss/yqID0jcv/oHU5oeAzAPA/7DV9e69FrP/fGJZAamndOvTjW'),
(3, NOW(6), NOW(6), 1, 'empleado@festival.com', '$2a$10$SRRTss/yqID0jcv/oHU5oeAzAPA/7DV9e69FrP/fGJZAamndOvTjW');

INSERT INTO user_role (created_at, updated_at, role, user_id) VALUES
(NOW(6), NOW(6), 'ADMINISTRADOR', 1),
(NOW(6), NOW(6), 'RESPONSABLE', 2),
(NOW(6), NOW(6), 'EMPLEADO', 3);

-- ---------------------------------------------------------------------
-- Responsable a cargo de la unidad 1 (Sabor Criollo)
-- ---------------------------------------------------------------------
INSERT INTO responsable (created_at, updated_at, nombre, apellido, dni, fecha_nacimiento, fecha_ingreso, unidad_de_venta_id, user_id) VALUES
(NOW(6), NOW(6), 'Carlos', 'Gómez', 30111222, '1985-04-12', '2026-01-10', 1, 2);

-- ---------------------------------------------------------------------
-- Empleado (Cajero) en la unidad 1. Su usuario es su email.
-- ---------------------------------------------------------------------
INSERT INTO staff (dtype, activo, nombre, apellido, dni, email, fecha_nacimiento, fecha_ingreso, sueldo, turno, horario_entrada, horario_salida, unidad_de_venta_id, user_id) VALUES
('Cajero', 1, 'Laura', 'Pérez', 35222333, 'empleado@festival.com', '1992-08-30', '2026-02-01', 450000.00, 'MAÑANA', '08:00:00', '16:00:00', 1, 3);

-- ---------------------------------------------------------------------
-- Verificación rápida (deberías ver: 5 / 25 / 125 / 122 / 5 / 3 / 3 / 1 / 1)
-- ---------------------------------------------------------------------
SELECT (SELECT COUNT(*) FROM festival)                  AS festivales,
       (SELECT COUNT(*) FROM unidad_de_venta)           AS unidades,
       (SELECT COUNT(*) FROM plato)                     AS platos_total,
       (SELECT COUNT(*) FROM plato WHERE activo = 1)    AS platos_activos,
       (SELECT COUNT(*) FROM costos)                    AS costos,
       (SELECT COUNT(*) FROM `user`)                    AS usuarios,
       (SELECT COUNT(*) FROM user_role)                 AS roles,
       (SELECT COUNT(*) FROM responsable)               AS responsables,
       (SELECT COUNT(*) FROM staff)                     AS empleados;

       admin@festival.com 1234 
       responsable@festival.com 1234
       empleado@festival.com 1234
