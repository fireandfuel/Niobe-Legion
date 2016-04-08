/*
 * Niobe Legion - a versatile client / server framework
 *     Copyright (C) 2013-2016 by fireandfuel (fireandfuel<at>hotmail<dot>de)
 *
 * This file (DependencyTest.java) is part of Niobe Legion (module niobe-legion-shared).
 *
 *     Niobe Legion is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Niobe Legion is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Niobe Legion. If not, see <http://www.gnu.org/licenses/>.
 */

package niobe.legion.shared.module;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import org.junit.Assert;
import org.junit.Test;

public class DependencyTest
{
    static String hostModuleName = "TestModule";
    static String hostModuleVersion = "1.0";
    List<TestModuleInstance> actualList;
    ArrayBlockingQueue<List<TestModuleInstance>> expectedLists;

    private void initOrderTestLists()
    {
        expectedLists = new ArrayBlockingQueue<List<TestModuleInstance>>(4);

        expectedLists.add(Arrays.asList(new TestModuleInstance(hostModuleName + "$" + hostModuleVersion,
                                                               "",
                                                               "test1",
                                                               "1",
                                                               "",
                                                               "A test module 1",
                                                               new File("test1.jar"),
                                                               "niobe.legion.shared.module.Test1"),
                                        new TestModuleInstance(hostModuleName + "$" + hostModuleVersion,
                                                               "",
                                                               "test3",
                                                               "1",
                                                               "",
                                                               "A test module 3",
                                                               new File("test3.jar"),
                                                               "niobe.legion.shared.module.Test3")));

        expectedLists.add(Arrays.asList(new TestModuleInstance(hostModuleName + "$" + hostModuleVersion + ", test1$1",
                                                               "",
                                                               "test2",
                                                               "1",
                                                               "",
                                                               "A test module 2, depends on test module 1",
                                                               new File("test2.jar"),
                                                               "niobe.legion.shared.module.Test2")));

        expectedLists.add(Arrays.asList(new TestModuleInstance(hostModuleName + "$" + hostModuleVersion + ", test2$1",
                                                               "",
                                                               "test4",
                                                               "1",
                                                               "",
                                                               "A test module 4, depends on test module 2",
                                                               new File("test4.jar"),
                                                               "niobe.legion.shared.module.Test4")));

        expectedLists
                .add(Arrays.asList(new TestModuleInstance(hostModuleName + "$" + hostModuleVersion + ", test3$1, test4$1, test2$1",
                                                          "",
                                                          "test5",
                                                          "1",
                                                          "",
                                                          "A test module 5, depends on test module 3, test module 4 and test module 2",
                                                          new File("test5.jar"),
                                                          "niobe.legion.shared.module.Test4")));

        this.actualList = Arrays
                .asList(new TestModuleInstance(hostModuleName + "$" + hostModuleVersion,
                                               "",
                                               "test1",
                                               "1",
                                               "",
                                               "A test module 1",
                                               new File("test1.jar"),
                                               "niobe.legion.shared.module.Test1"),
                        new TestModuleInstance(hostModuleName + "$" + hostModuleVersion + ", test1$1",
                                               "",
                                               "test2",
                                               "1",
                                               "",
                                               "A test module 2, depends on test module 1",
                                               new File("test2.jar"),
                                               "niobe.legion.shared.module.Test2"),
                        new TestModuleInstance(hostModuleName + "$" + hostModuleVersion,
                                               "",
                                               "test3",
                                               "1",
                                               "",
                                               "A test module 3",
                                               new File("test3.jar"),
                                               "niobe.legion.shared.module.Test3"),
                        new TestModuleInstance(hostModuleName + "$" + hostModuleVersion + ", test2$1",
                                               "",
                                               "test4",
                                               "1",
                                               "",
                                               "A test module 4, depends on test module 2",
                                               new File("test4.jar"),
                                               "niobe.legion.shared.module.Test4"),
                        new TestModuleInstance(hostModuleName + "$" + hostModuleVersion + ", test3$1, test4$1, test2$1",
                                               "",
                                               "test5",
                                               "1",
                                               "",
                                               "A test module 5, depends on test module 3, test module 4 and test module 2",
                                               new File("test5.jar"),
                                               "niobe.legion.shared.module.Test4"));
    }

    @Test
    public void testDependencyInOrder()
    {
        initOrderTestLists();

        TestModuleLoader loader = new TestModuleLoader(hostModuleName, hostModuleVersion, null, this.actualList);
        loader.buildDependencies(this.actualList);
        checkOrderTestResults(loader.buildDependencyQueue());
    }

    @Test
    public void testDependencyReverseOrder()
    {
        initOrderTestLists();
        Collections.reverse(this.actualList);

        TestModuleLoader loader = new TestModuleLoader(hostModuleName, hostModuleVersion, null, this.actualList);
        loader.buildDependencies(this.actualList);
        checkOrderTestResults(loader.buildDependencyQueue());
    }

    @Test
    public void testDependencyRandomOrder()
    {
        initOrderTestLists();
        Collections.shuffle(this.actualList, new Random(3467894236L)); // looks like a relatively good seed for random

        TestModuleLoader loader = new TestModuleLoader(hostModuleName, hostModuleVersion, null, this.actualList);
        loader.buildDependencies(this.actualList);
        checkOrderTestResults(loader.buildDependencyQueue());
    }

    private void checkOrderTestResults(ArrayBlockingQueue<List<TestModuleInstance>> actualLists)
    {
        Assert.assertNotNull(actualLists);
        Assert.assertEquals(expectedLists.size(), actualLists.size());
        for(int listIndex = 0; listIndex < actualLists.size(); listIndex++)
        {
            List<TestModuleInstance> expectedList = expectedLists.poll();
            List<TestModuleInstance> actualList = actualLists.poll();
            Assert.assertEquals(expectedList.size(), actualList.size());

            // the order of items with same priority may differ, but it does not care.
            expectedList.forEach(item -> Assert.assertTrue(actualList.contains(item)));
        }
    }

    @Test
    public void testSelfDependency()
    {
        this.actualList = Arrays.asList(new TestModuleInstance(hostModuleName + "$" + hostModuleVersion + ", test1$1",
                                                               "",
                                                               "test1",
                                                               "1",
                                                               "",
                                                               "A test module 1",
                                                               new File("test1.jar"),
                                                               "niobe.legion.shared.module.Test1"));

        TestModuleLoader loader = new TestModuleLoader(hostModuleName, hostModuleVersion, null, this.actualList);
        loader.buildDependencies(this.actualList);
        ArrayBlockingQueue<List<TestModuleInstance>> list = loader.buildDependencyQueue();
        Assert.assertTrue(list.size() == 1);
    }

    @Test
    public void testForwardDependencyCycle()
    {
        this.actualList = Arrays.asList(new TestModuleInstance(hostModuleName + "$" + hostModuleVersion + ", test2$1",
                                                               "",
                                                               "test1",
                                                               "1",
                                                               "",
                                                               "A test module 1, depends on test module 2",
                                                               new File("test1.jar"),
                                                               "niobe.legion.shared.module.Test1"),
                                        new TestModuleInstance(hostModuleName + "$" + hostModuleVersion + ", test3$1",
                                                               "",
                                                               "test2",
                                                               "1",
                                                               "",
                                                               "A test module 2, depends on test module 3",
                                                               new File("test2.jar"),
                                                               "niobe.legion.shared.module.Test2"),
                                        new TestModuleInstance(hostModuleName + "$" + hostModuleVersion + ", test1$1",
                                                               "",
                                                               "test3",
                                                               "1",
                                                               "",
                                                               "A test module 3, depends on test module 1",
                                                               new File("test3.jar"),
                                                               "niobe.legion.shared.module.Test3"));

        TestModuleLoader loader = new TestModuleLoader(hostModuleName, hostModuleVersion, null, this.actualList);
        loader.buildDependencies(this.actualList);
        ArrayBlockingQueue<List<TestModuleInstance>> list = loader.buildDependencyQueue();
        Assert.assertNull(list);
        this.actualList.forEach(item -> Assert.assertEquals(ModuleInstance.DEPENDENCY_CYCLE, item.getState()));
    }

    @Test
    public void testBackwardDependencyCycle()
    {
        this.actualList = Arrays.asList(new TestModuleInstance(hostModuleName + "$" + hostModuleVersion + ", test3$1",
                                                               "",
                                                               "test1",
                                                               "1",
                                                               "",
                                                               "A test module 1, depends on test module 3",
                                                               new File("test1.jar"),
                                                               "niobe.legion.shared.module.Test1"),
                                        new TestModuleInstance(hostModuleName + "$" + hostModuleVersion + ", test1$1",
                                                               "",
                                                               "test2",
                                                               "1",
                                                               "",
                                                               "A test module 2, depends on test module 1",
                                                               new File("test2.jar"),
                                                               "niobe.legion.shared.module.Test2"),
                                        new TestModuleInstance(hostModuleName + "$" + hostModuleVersion + ", test2$1",
                                                               "",
                                                               "test3",
                                                               "1",
                                                               "",
                                                               "A test module 3, depends on test module 2",
                                                               new File("test3.jar"),
                                                               "niobe.legion.shared.module.Test3"));

        TestModuleLoader loader = new TestModuleLoader(hostModuleName, hostModuleVersion, null, this.actualList);
        loader.buildDependencies(this.actualList);
        ArrayBlockingQueue<List<TestModuleInstance>> list = loader.buildDependencyQueue();
        Assert.assertNull(list);
        this.actualList.forEach(item -> Assert.assertEquals(ModuleInstance.DEPENDENCY_CYCLE, item.getState()));
    }

    @Test
    public void testTwoItemCycle()
    {
        this.actualList = Arrays.asList(new TestModuleInstance(hostModuleName + "$" + hostModuleVersion + ", test2$1",
                                                               "",
                                                               "test1",
                                                               "1",
                                                               "",
                                                               "A test module 1, depends on test module 2",
                                                               new File("test1.jar"),
                                                               "niobe.legion.shared.module.Test1"),
                                        new TestModuleInstance(hostModuleName + "$" + hostModuleVersion + ", test3$1",
                                                               "",
                                                               "test2",
                                                               "1",
                                                               "",
                                                               "A test module 2, depends on test module 3",
                                                               new File("test2.jar"),
                                                               "niobe.legion.shared.module.Test2"),
                                        new TestModuleInstance(hostModuleName + "$" + hostModuleVersion + ", test2$1",
                                                               "",
                                                               "test3",
                                                               "1",
                                                               "",
                                                               "A test module 3, depends on test module 2",
                                                               new File("test3.jar"),
                                                               "niobe.legion.shared.module.Test3"));

        TestModuleLoader loader = new TestModuleLoader(hostModuleName, hostModuleVersion, null, this.actualList);
        loader.buildDependencies(this.actualList);
        ArrayBlockingQueue<List<TestModuleInstance>> list = loader.buildDependencyQueue();
        Assert.assertNull(list);
        this.actualList.forEach(item -> Assert.assertEquals(ModuleInstance.DEPENDENCY_CYCLE, item.getState()));
    }
}
