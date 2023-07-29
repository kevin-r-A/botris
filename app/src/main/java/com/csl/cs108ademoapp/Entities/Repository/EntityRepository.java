package com.csl.cs108ademoapp.Entities.Repository;

import android.app.Application;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.csl.cs108ademoapp.Entities.DAO.EntityDao;
import com.csl.cs108ademoapp.Entities.DATABASE;
import com.csl.cs108ademoapp.Entities.Enums.DaoType;
import com.csl.cs108ademoapp.Web.Repository.WebServiceRepository;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class EntityRepository<T, U extends EntityDao<T>> {
    protected WebServiceRepository webServiceRepository;
    protected DATABASE db;
    protected U entityDao;
    protected String TableName = "";
    protected T entity;
    protected List<T> entityList;
    protected String id;

    public EntityRepository(Application application) {
        db = DATABASE.getDatabase(application);
        webServiceRepository = new WebServiceRepository(application);
    }

    public boolean InsertSync(final T entity) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                entityDao.InsertWithChild(entity);
            }
        });
        t.start();
        try {
            t.join();
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean InsertAllSync(final List<T> entity) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                entityDao.InsertAllWithChild(entity);
            }
        });
        t.start();
        try {
            t.join();
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean UpdateSync(final T entity) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                entityDao.UpdateWithChild(entity);
            }
        });
        t.start();
        try {
            t.join();
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void UpdateAllSync(final List<T> entity) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                entityDao.InsertAllWithChild(entity);
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void InsertAsync(T entity) {
        List<T> tList = new ArrayList<>();
        tList.add(entity);
        new GeneralAsync(entityDao, DaoType.Insert).execute(tList);
    }

    public void InsertAllAsync(List<T> entities) {
        new GeneralAsync(entityDao, DaoType.InsertAll).execute(entities);
    }

    public void UpdateAsync(T entity) {
        List<T> tList = new ArrayList<>();
        tList.add(entity);
        new GeneralAsync(entityDao, DaoType.Update).execute(tList);
    }

    public void UpdateAllAsync(List<T> entities) {
        new GeneralAsync(entityDao, DaoType.UpdateAll).execute(entities);
    }

    public void DeleteAsync(T entity) {
        List<T> tList = new ArrayList<>();
        tList.add(entity);
        new GeneralAsync(entityDao, DaoType.Delete).execute(tList);
    }

    public void DeleteAllAsync(List<T> entities) {
        new GeneralAsync(entityDao, DaoType.DeleteAll).execute(entities);
    }

    public void DeleteAllSync(final List<T> entity) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                entityDao.DeleteAll(entity);
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public List<T> GetAll() {
        final List<T>[] result = new List[]{new ArrayList<>()};
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                result[0] = entityDao.GetAll();
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result[0];
    }

    public void Download(final String name, boolean async, @Nullable final Object... args) {
        Download1(name, async, true, args);
    }

    public void Download1(final String name, boolean async, boolean delete, @Nullable final Object... args) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if (delete) {
                    List<T> tListActual = entityDao.GetAll();
                    entityDao.DeleteAllWithChild(tListActual);
                }
                List<T> tList = CallFunction(new ArrayList<T>(), webServiceRepository, name, args);
                if (tList != null) {
                    InsertAllSync(tList);
                }
            }
        });
        t.start();
        if (!async) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public <V> V CallFunction(V tclass, Object instance, String name, @Nullable Object... args) {
        if (args != null) {
            Class<?>[] params = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Integer) {
                    params[i] = Integer.TYPE;
                } else if (args[i] instanceof String) {
                    params[i] = String.class;
                } else {
                    params[i] = args[i].getClass();
                }
            }
            try {
                Method method = instance.getClass().getMethod(name, params);
                return (V) method.invoke(instance, args);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            try {
                Method method = instance.getClass().getMethod(name, null);
                return (V) method.invoke(instance, args);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        }

    }

    private <V> V MakeCall(Call<V> requestCall) {
        try {
            Response<V> pagedResultDtoResponse = requestCall.execute();
            if (pagedResultDtoResponse.isSuccessful()) {
                return pagedResultDtoResponse.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private class GeneralAsync extends AsyncTask<List<T>, Void, Void> {
        EntityDao<T> entityDao;
        DaoType daoType;

        public GeneralAsync(EntityDao<T> entityDao, DaoType daoType) {
            this.entityDao = entityDao;
            this.daoType = daoType;
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(List<T>... ts) {
            switch (daoType) {
                case Insert:
                    entityDao.InsertWithChild(ts[0].get(0));
                    break;
                case InsertAll:
                    entityDao.InsertAllWithChild(ts[0]);
                    break;
                case Update:
                    entityDao.UpdateWithChild(ts[0].get(0));
                    break;
                case UpdateAll:
                    entityDao.UpdateAllWithChild(ts[0]);
                    break;
                case Delete:
                    entityDao.DeleteWithChild(ts[0].get(0));
                    break;
                case DeleteAll:
                    entityDao.DeleteAllWithChild(ts[0]);
                    break;
            }
            return null;
        }
    }
}
