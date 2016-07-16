package model;
/**
 * Repository intermediate implementation. CRUD methods all manages the same
 * type of object.
 * @author stg
 *
 * @param <E>: Type of object managed by the Repository
 */
public abstract class UniRepository<E> extends Repository<E, E, E, E> { }