package com.magenta.guice.override;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import org.junit.Test;

import javax.annotation.Nullable;

import static org.junit.Assert.*;

/*
* Project: Maxifier
* Author: Aleksey Didik
* Created: 23.05.2008 10:19:35
* 
* Copyright (c) 1999-2009 Magenta Corporation Ltd. All Rights Reserved.
* Magenta Technology proprietary and confidential.
* Use is subject to license terms.
*/

public class OverrideModuleTest {
    @Test
    public void testSimpleOverride() throws Exception {
        Module base = new AbstractModule() {
            @Override
            protected void configure() {
                bind(I.class).to(OldImpl.class);
            }
        };

        Module override = new OverrideModule() {


            @Nullable
            @Override
            public Module override() {
                return new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(I.class).to(NewImpl.class);
                    }
                };
            }

            @Override
            public void configure(Binder binder) {

            }
        };

        Injector inj = Guice.createInjector(GuiceOverrides.collect(base, override));
        I instance = inj.getInstance(I.class);
        assertTrue(instance instanceof NewImpl);
    }

    @Test
    public void testScopeOverride() throws Exception {
        Module base = new AbstractModule() {
            @Override
            protected void configure() {
                bind(I.class).to(OldImpl.class).in(Scopes.SINGLETON);
            }
        };

        Module override = new OverrideModule() {

            @Nullable
            @Override
            public Module override() {
                return new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(I.class).to(OldImpl.class).in(Scopes.NO_SCOPE);
                    }
                };
            }

            @Override
            public void configure(Binder binder) {

            }
        };

        Injector inj = Guice.createInjector(GuiceOverrides.collect(base, override));
        I instance1 = inj.getInstance(I.class);
        I instance2 = inj.getInstance(I.class);
        assertTrue(instance1 instanceof OldImpl);
        assertTrue(instance2 instanceof OldImpl);
        assertFalse(instance1.equals(instance2));
    }

    static interface I {
    }

    static class OldImpl implements I {
    }

    static class NewImpl implements I {
    }

    @Test
    public void testPrivateModulesOverride() {
        Module pm1 = new PrivateModule() {
            @Override
            protected void configure() {
                bind(I.class).to(OldImpl.class);
                expose(I.class);

            }
        };
        Module pm2 = new PrivateModule() {
            @Override
            protected void configure() {
                bind(I.class).to(NewImpl.class);
                expose(I.class);
            }
        };

    }

    @Test
    public void testConstantOverride() throws Exception {
        Module m1 = new AbstractModule() {
            @Override
            protected void configure() {
                bindConstant().annotatedWith(Names.named("a")).to("Hello");
            }
        };

        Module m2 = new OverrideModule() {

            @Nullable
            @Override
            public Module override() {
                return new AbstractModule() {
                    @Override
                    protected void configure() {
                        bindConstant().annotatedWith(Names.named("a")).to("world");
                    }
                };
            }

            @Override
            public void configure(Binder binder) {

            }
        };

        Foo instance = Guice.createInjector(GuiceOverrides.collect(m1, m2)).getInstance(Foo.class);
        assertEquals(instance.bu, "world");
    }

    static class Foo {
        @Inject
        @Named("a")
        String bu;
    }

    @Test
    public void testWithProvides() throws Exception {
        Module m = new AbstractModule() {
            @Override
            public void configure() {
                bind(String.class).annotatedWith(Names.named("foo")).toInstance("Hello");
            }

            @Provides
            @Named("a")
            public String provideA(@Named("foo") String foo) {
                return foo + " world";
            }
        };

        Foo instance = Guice.createInjector(GuiceOverrides.collect(m)).getInstance(Foo.class);
        assertEquals(instance.bu, "Hello world");
    }
}
