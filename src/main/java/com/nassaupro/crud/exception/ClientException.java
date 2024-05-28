package com.nassaupro.crud.exception;

public class ClientException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ClientException(String message) {
        super(message);
    }

    public static ClientException clientNotFound(Long id) {
        return new ClientException("Cliente não encontrado com o ID: " + id);
    }

	public static Exception cpfAlreadyExists(String cpf) {
		return new ClientException("O CPF " + cpf + " já está em uso");
	}

	public static Exception invalidCpfLength() {
		return new ClientException("O tamanho do CPF não é válido");
	}

}

