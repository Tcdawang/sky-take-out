package com.sky.exception;

public class EmployeeIsExistException extends BaseException{
    public EmployeeIsExistException(){}

    public EmployeeIsExistException(String msg){
        super(msg);
    }
}
