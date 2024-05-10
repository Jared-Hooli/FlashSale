package com.czj.dev.exception;

import com.czj.dev.result.CodeMsg;

public class FlashSaleException extends RuntimeException {
	private final CodeMsg codeMsg;

	public FlashSaleException(CodeMsg codeMsg) {
		super(codeMsg.getMsg());
		this.codeMsg = codeMsg;
	}

	public CodeMsg getCodeMsg() {
		return codeMsg;
	}
}
