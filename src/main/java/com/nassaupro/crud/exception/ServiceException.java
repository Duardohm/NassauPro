package com.nassaupro.crud.exception;

public class ServiceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ServiceException(String message) {
        super(message);
    }

    public static ServiceException serviceNotFound(Long id) {
        return new ServiceException("Serviço não encontrado com o ID: " + id);
    }
}
