package com.example.demo.controller;

import com.diboot.core.controller.BaseCrudRestController;
import com.diboot.core.entity.AbstractEntity;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义通用CRUD父类RestController
 *
 * @author MyName
 * @version 1.0
 * @date 2022-12-30
 * Copyright © MyCompany
 */
@Slf4j
public class BaseCustomCrudRestController<E extends AbstractEntity<?>> extends BaseCrudRestController<E> {

}