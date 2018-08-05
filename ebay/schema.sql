DROP DATABASE IF EXISTS ebay;
CREATE DATABASE ebay;

\c ebay;

-- DROP TABLE IF EXISTS "Usuario";
-- DROP TABLE IF EXISTS "Produto";
-- DROP TABLE IF EXISTS "Publicacao";
-- DROP TABLE IF EXISTS "Foto";
-- DROP TABLE IF EXISTS "Avaliacao";
-- DROP TABLE IF EXISTS "Compra";
-- DROP TABLE IF EXISTS "ItemVenda";
-- DROP TABLE IF EXISTs "Cobaia";

CREATE TABLE "Usuario" (
	"ID"        SERIAL, 
	"Nome"      VARCHAR(100)                             UNIQUE NOT NULL, 
	"Email"     VARCHAR(50)                              UNIQUE NOT NULL,
	"Login"     VARCHAR(30)                              UNIQUE NOT NULL, 
	"Senha"     VARCHAR(32)                                     NOT NULL, 
	"Endereco"  VARCHAR(500)                                    NOT NULL,
	"DateTimeCreation" 	 TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
	"DateTimeLastUpdate" TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
	CONSTRAINT "UsuarioPK" PRIMARY KEY ("ID")
);

CREATE TABLE "Produto" (
	"ID"        	SERIAL,
	"Nome"      	VARCHAR(100)                                NOT NULL,
	"Categoria" 	VARCHAR(50)                                 NOT NULL,
	"Descricao" 	TEXT                                            NULL,
	"DateTimeCreation" 	 TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
	"DateTimeLastUpdate" TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
	CONSTRAINT "ProdutoPK" PRIMARY KEY ("ID")
);

CREATE TABLE "Publicacao" (
	"ID"         SERIAL,
	"Valor"      NUMERIC(11,2)                                  NOT NULL,
	"Quantidade" INT                                            NOT NULL,
	"Descricao"  TEXT                                           NOT NULL,
	"Data"       		 TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
	"IDUsuario"  INTEGER                                        NOT NULL,
	"IDProduto"  INTEGER                                        NOT NULL,
	"DateTimeCreation" 	 TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
	"DateTimeLastUpdate" TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
	CONSTRAINT "PublicacaoPK" PRIMARY KEY ("ID"),
	CONSTRAINT "IDUsuarioFK"  FOREIGN KEY ("IDUsuario") 
		REFERENCES "Usuario" ("ID")
		ON DELETE CASCADE,
	CONSTRAINT "IDProdutoFK"  FOREIGN KEY ("IDProduto") 
		REFERENCES "Produto" ("ID")
		ON DELETE CASCADE,
	CONSTRAINT "CheckValor"      CHECK ("Valor" > 0),
	CONSTRAINT "CheckQuantidade" CHECK ("Quantidade" >= 0)
);

CREATE TABLE "Foto" (
	"ID"            SERIAL,
	"Foto"          BYTEA                                       NOT NULL,
	"IDPublicacao"  INTEGER                                     NOT NULL,
	"DateTimeCreation" 	 TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
	"DateTimeLastUpdate" TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
	CONSTRAINT "FotoPK" PRIMARY KEY ("ID"),
	CONSTRAINT "IDPublicacaoFK" FOREIGN KEY ("IDPublicacao") 
		REFERENCES "Publicacao" ("ID")
		ON DELETE CASCADE
);
	
CREATE TABLE "Avaliacao" (
	"ID"            SERIAL,
	"Pontuacao"     SMALLINT                                    NOT NULL,
	"Comentario"    TEXT,
	"IDPublicacao"  INTEGER                                     NOT NULL,
	"IDUsuario"     INTEGER                                     NOT NULL,
	"DateTimeCreation" 	 TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
	"DateTimeLastUpdate" TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
	CONSTRAINT "AvaliacaoPK"    PRIMARY KEY ("ID"),
	CONSTRAINT "IDPublicacaoFK" FOREIGN KEY ("IDPublicacao") 
		REFERENCES "Publicacao"("ID")
		ON DELETE CASCADE,
	CONSTRAINT "IDUsuario" FOREIGN KEY ("IDUsuario") 
		REFERENCES "Usuario"("ID")
		ON DELETE CASCADE,
	CONSTRAINT "CheckAvaliacao"  CHECK ("Pontuacao" > 0),
	CONSTRAINT "CheckAvaliacao1" CHECK ("Pontuacao" <= 5)
);

CREATE TABLE "Compra" (
	"ID"        SERIAL,
	"Data"               TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
	"IDUsuario" INTEGER                                         NOT NULL,
	"DateTimeCreation" 	 TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
	"DateTimeLastUpdate" TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
	CONSTRAINT "CompraPK" PRIMARY KEY ("ID"),
	CONSTRAINT "IDUsuarioFK" FOREIGN KEY ("IDUsuario") 
		REFERENCES "Usuario"("ID")
		ON DELETE CASCADE
);

CREATE TABLE "ItemVenda" (
	"ID"            SERIAL,
	"Quantidade"    INT                                         NOT NULL,
	"IDCompra"      INTEGER                                     NOT NULL,
	"IDPublicacao"  INTEGER                                     NOT NULL,
	"DateTimeCreation" 	 TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
	"DateTimeLastUpdate" TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
	CONSTRAINT "ItemVendaPK" PRIMARY KEY ("ID"),
	CONSTRAINT "CheckQuantidade" CHECK ("Quantidade" > 0),
	CONSTRAINT "IDCompraFK" FOREIGN KEY ("IDCompra") 
		REFERENCES "Compra"("ID")
		ON DELETE CASCADE,
	CONSTRAINT "IDPublicacao" FOREIGN KEY ("IDPublicacao") 
		REFERENCES "Publicacao"("ID")
		ON DELETE CASCADE
);

CREATE TABLE "Cobaia" (
	"ID" SERIAL, 		                                     
	"Nome" VARCHAR(30)                                          NOT NULL,
	"DateTimeCreation" 	 TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
	"DateTimeLastUpdate" TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
	CONSTRAINT "CobaiaPK" PRIMARY KEY ("ID")
);

CREATE OR REPLACE FUNCTION checkQuantidadeProdutos() RETURNS TRIGGER AS
$$
DECLARE
	quantidade INT;
BEGIN
	SELECT "Quantidade" INTO quantidade FROM "Publicacao" WHERE "ID" = NEW."IDPublicacao";
	IF quantidade < NEW."Quantidade" THEN
		RAISE EXCEPTION 'Quantidade de produtos insuficiente.';
	ELSE
		UPDATE "Publicacao" SET "Quantidade" = "Quantidade" - NEW."Quantidade" 
		WHERE "ID" = NEW."IDPublicacao"; 
		RETURN NEW;
	END IF;
END;
$$ LANGUAGE 'plpgsql';

CREATE TRIGGER checkQuantidade BEFORE INSERT
ON "ItemVenda" FOR EACH ROW EXECUTE PROCEDURE checkQuantidadeProdutos();
