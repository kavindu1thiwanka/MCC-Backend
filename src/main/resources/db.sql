CREATE DATABASE IF NOT EXISTS `business_management_system`;

USE `business_management_system`;

CREATE TABLE IF NOT EXISTS `address_mst` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `user_id` int(11) DEFAULT NULL,
    `address_line_1` varchar(255) DEFAULT NULL,
    `address_line_2` varchar(255) DEFAULT NULL,
    `city` varchar(255) DEFAULT NULL,
    `state` varchar(255) DEFAULT NULL,
    `country` varchar(255) DEFAULT NULL,
    `postal_code` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

CREATE TABLE IF NOT EXISTS `common_email_mst` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `send_to` varchar(255) NOT NULL,
    `subject` varchar(255) NOT NULL,
    `content` text NOT NULL,
    `retry_count` int(11) DEFAULT NULL,
    `status` char(1) NOT NULL,
    `created_by` varchar(255) DEFAULT NULL,
    `created_on` datetime(6) DEFAULT NULL,
    `update_by` varchar(255) DEFAULT NULL,
    `update_on` datetime(6) DEFAULT NULL,
    `delete_by` varchar(255) DEFAULT NULL,
    `delete_on` datetime(6) DEFAULT NULL,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

CREATE TABLE IF NOT EXISTS `common_email_template` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `subject` varchar(255) NOT NULL,
    `template_data` text NOT NULL,
    `status` char(1) NOT NULL,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

CREATE TABLE IF NOT EXISTS `menu_mst` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `name` varchar(255) DEFAULT NULL,
    `main_role` int(11) DEFAULT NULL,
    `parent_id` int(11) DEFAULT NULL,
    `display_order` int(11) DEFAULT NULL,
    `type` char(1) DEFAULT NULL,
    `status` char(1) DEFAULT NULL,
    `icon` varchar(255) DEFAULT NULL,
    `route` varchar(255) DEFAULT NULL,
    `created_by` varchar(255) DEFAULT NULL,
    `created_on` datetime(6) DEFAULT NULL,
    `update_by` varchar(255) DEFAULT NULL,
    `update_on` datetime(6) DEFAULT NULL,
    `delete_by` varchar(255) DEFAULT NULL,
    `delete_on` datetime(6) DEFAULT NULL,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

CREATE TABLE IF NOT EXISTS `menu_privileges` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `menu_id` int(11) DEFAULT NULL,
    `privilege_id` int(11) DEFAULT NULL,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

CREATE TABLE IF NOT EXISTS `privilege_mst` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `display_name` varchar(255) DEFAULT NULL,
    `privilege_code` varchar(255) DEFAULT NULL,
    `privilege_name` varchar(255) DEFAULT NULL,
    `status` char(1) DEFAULT NULL,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

CREATE TABLE IF NOT EXISTS `reservation_mst` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `user_id` int(11) NOT NULL,
    `vehicle_no` varchar(255) NOT NULL,
    `driver_id` int(11) DEFAULT NULL,
    `pick_up_location` varchar(255) NOT NULL,
    `return_location` varchar(255) NOT NULL,
    `pick_up_date` datetime(6) NOT NULL,
    `return_date` datetime(6) NOT NULL,
    `payment_status` char(1) NOT NULL,
    `status` char(1) NOT NULL,
    `created_by` varchar(255) DEFAULT NULL,
    `created_on` datetime(6) DEFAULT NULL,
    `update_by` varchar(255) DEFAULT NULL,
    `update_on` datetime(6) DEFAULT NULL,
    `delete_by` varchar(255) DEFAULT NULL,
    `delete_on` datetime(6) DEFAULT NULL,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

CREATE TABLE IF NOT EXISTS `role_mst` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `role_name` varchar(255) DEFAULT NULL,
    `main_role` int(11) DEFAULT NULL,
    `status` char(1) DEFAULT NULL,
    `created_by` varchar(255) DEFAULT NULL,
    `created_on` datetime(6) DEFAULT NULL,
    `update_by` varchar(255) DEFAULT NULL,
    `update_on` datetime(6) DEFAULT NULL,
    `delete_by` varchar(255) DEFAULT NULL,
    `delete_on` datetime(6) DEFAULT NULL,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

CREATE TABLE IF NOT EXISTS `role_privileges` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `privilege_id` int(11) DEFAULT NULL,
    `role_id` int(11) DEFAULT NULL,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

CREATE TABLE IF NOT EXISTS `transaction_mst` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `reservation_id` int(11) NOT NULL,
    `amount` decimal(38,2) NOT NULL,
    `payment_type` int(11) NOT NULL,
    `payment_date` datetime(6) NOT NULL,
    `status` char(1) NOT NULL,
    `created_by` varchar(255) DEFAULT NULL,
    `created_on` datetime(6) DEFAULT NULL,
    `update_by` varchar(255) DEFAULT NULL,
    `update_on` datetime(6) DEFAULT NULL,
    `delete_by` varchar(255) DEFAULT NULL,
    `delete_on` datetime(6) DEFAULT NULL,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

CREATE TABLE IF NOT EXISTS `user_mst` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `uuid` varchar(255) NOT NULL,
    `username` varchar(255) NOT NULL,
    `first_name` varchar(255) NOT NULL,
    `last_name` varchar(255) NOT NULL,
    `email` varchar(255) NOT NULL,
    `password` varchar(255) NOT NULL,
    `role_id` int(11) NOT NULL,
    `contact_number` varchar(255) DEFAULT NULL,
    `driver_license_no` varchar(255) DEFAULT NULL,
    `status` char(1) NOT NULL,
    `created_by` varchar(255) DEFAULT NULL,
    `created_on` datetime(6) DEFAULT NULL,
    `update_by` varchar(255) DEFAULT NULL,
    `update_on` datetime(6) DEFAULT NULL,
    `delete_by` varchar(255) DEFAULT NULL,
    `delete_on` datetime(6) DEFAULT NULL,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

CREATE TABLE IF NOT EXISTS `user_wise_roles` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `role_id` int(11) DEFAULT NULL,
    `user_id` int(11) DEFAULT NULL,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

CREATE TABLE IF NOT EXISTS `vehicle_mst` (
    `vehicle_no` varchar(255) NOT NULL,
    `vehicle_model` varchar(255) NOT NULL,
    `vehicle_type` varchar(255) NOT NULL,
    `price` decimal(38,2) NOT NULL,
    `availability` char(1) NOT NULL,
    `seats` int(11) NOT NULL,
    `gear_type` varchar(255) NOT NULL,
    `category` varchar(255) NOT NULL,
    `status` char(1) NOT NULL,
    `vehicle_image` varchar(255) NOT NULL,
    `created_by` varchar(255) DEFAULT NULL,
    `created_on` datetime(6) DEFAULT NULL,
    `update_by` varchar(255) DEFAULT NULL,
    `update_on` datetime(6) DEFAULT NULL,
    `delete_by` varchar(255) DEFAULT NULL,
    `delete_on` datetime(6) DEFAULT NULL,
    PRIMARY KEY (`vehicle_no`)
    ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;