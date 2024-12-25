package com.katbrew.services.tables;

import com.katbrew.entities.jooq.db.tables.daos.UsersDao;
import com.katbrew.entities.jooq.db.tables.pojos.Users;
import com.katbrew.entities.jooq.db.tables.records.UsersRecord;
import com.katbrew.services.base.JooqService;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

/**
 * Der Service der Datenbanktabelle users.
 */
@Service
@Slf4j
public class UsersService extends JooqService<Users, UsersRecord> {
    public UsersService(final DSLContext context) {
        super(new UsersDao(), context);
    }

//    /**
//     * Liefert den Session User, der die Anfrage gestellt hat.
//     *
//     * @return Der aktuell angemeldete User.
//     */
//    public Users getCurrentUser() {
//        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        return findBy(Collections.singletonList(
//                Tables.USERS.USERNAME.eq(authentication.getPrincipal().toString())
//        )).get(0);
//    }
//
//    /**
//     * Updated einen User in der Datenbank.
//     *
//     * @param entityToUpdate Das Object, das geupdated werden soll.
//     */
//    @Override
//    public void update(Users entityToUpdate) {
//        final Users UsersInDatabase = findOne(entityToUpdate.getId());
//
//        if (!UsersInDatabase.getUsername().equals(entityToUpdate.getUsername())) {
//            final List<Users> UsersWithUsername = findBy(
//                    Collections.singletonList(Tables.USERS.USERNAME.eq(entityToUpdate.getUsername()))
//            );
//            if (UsersWithUsername.size() != 0) {
//                throw new UniqueConstrainedFailedException(Tables.USERS.USERNAME);
//            }
//        }
//
//        if (entityToUpdate.getPassword().isEmpty()) {
//            entityToUpdate.setPassword(UsersInDatabase.getPassword());
//        }
//        super.update(entityToUpdate);
//    }
//
//    /**
//     * Erstellt einen Neuen Nutzer in der Datenbank.
//     *
//     * @param entityToInsert Das Object, das in die Datenbank geschrieben werden soll.
//     */
//    @Override
//    public void insert(Users entityToInsert) {
//        final List<Users> Users = findBy(
//                Collections.singletonList(Tables.USERS.USERNAME.eq(entityToInsert.getUsername()))
//        );
//
//        if (Users.size() != 0) {
//            throw new UniqueConstrainedFailedException(Tables.USERS.USERNAME);
//        }
//        super.insert(entityToInsert);
//    }
//
//    /**
//     * Schreibt eine Liste von Usern in die Datenbank.
//     *
//     * @param entitiesToInsert Die Liste der Entities, die zurückgeschrieben wird.
//     */
//    @Override
//    public void insert(List<Users> entitiesToInsert) {
//        entitiesToInsert.forEach(this::insert);
//    }
//
//    /**
//     * Liefert die Id eines Users zum Username.
//     *
//     * @param username Der Username nach dem gesucht wird.
//     * @return Die Id des gefundenen Users.
//     */
//    public int getUserIdByUsername(final String username) {
//        return this.findBy(Collections.singletonList(
//                Tables.USERS.USERNAME.eq(username)
//        )).get(0).getId();
//    }

}
