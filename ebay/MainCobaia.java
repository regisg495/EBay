package ebay;

import ebay.exception.EntityTransientException;
import ebay.modelo.Cobaia;
import ebay.persistencia.CobaiaDAO;
import ebay.persistencia.IDAO;
import ebay.persistencia.IEntity;

public class MainCobaia {
	public static void main(String[] args) throws EntityTransientException  {
		
		// CobaiaDAO implements IDAO<Cobaia>
		IDAO<Cobaia> dao = new CobaiaDAO();
		Cobaia c = new Cobaia();
		c.setNome("Teste");
		// Cobaia implements IEntity
		System.out.println((c instanceof IEntity) == true);
		// c não é persistente
		System.out.println(c.isPersistent() == false);
		// c é transiente
		System.out.println(c.isTransient() == true);
		// c não tem id
		System.out.println(c.getId() == null);
		// c é salvo
		dao.save(c);
		// c é persistente
		System.out.println(c.isPersistent() == true);
		// c tem id
		System.out.println(c.getId() > 0);
		// c é excluído
		dao.delete(c);
		// c não é persistente mais
		System.out.println(c.isPersistent() == false);
		// c é transiente
		System.out.println(c.isTransient() == true);
		// c não tem id
		System.out.println(c.getId() == null);
	}
}
