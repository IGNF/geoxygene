package fr.ign.cogit.geoxygene.appli.task;

import org.junit.Test;

import junit.framework.Assert;

public class TaskManagerTest {

    @Test
    public void testStartAndWait() {
        FakeTask task = new FakeTask("fake task 0", 100, 10);
        Assert.assertEquals(TaskState.WAITING, task.getState());
        try {
            TaskManager.startAndWait(task);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.assertTrue("exception thrown : " + e.getMessage(), false);
        }
        Assert.assertEquals(TaskState.FINISHED, task.getState());
        Assert.assertEquals(1., task.getProgress());
        Assert.assertEquals(0, task.getTaskListenerCount());

    }

    @Test
    public void testWaitForCompletion() {
        FakeTask task1 = new FakeTask("fake task 1", 1, 1);
        Assert.assertEquals(TaskState.WAITING, task1.getState());
        task1.start();
        try {
            TaskManager.waitForCompletion(task1);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.assertTrue("exception thrown : " + e.getMessage(), false);
        }
        Assert.assertEquals(TaskState.FINISHED, task1.getState());
        Assert.assertEquals(1., task1.getProgress());
        Assert.assertEquals(0, task1.getTaskListenerCount());

        FakeTask task2 = new FakeTask("fake task 2", 10, 100);
        Assert.assertEquals(TaskState.WAITING, task2.getState());
        task2.start();
        try {
            TaskManager.waitForCompletion(task2);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.assertTrue("exception thrown : " + e.getMessage(), false);
        }
        Assert.assertEquals(TaskState.FINISHED, task2.getState());
        Assert.assertEquals(1., task2.getProgress());
        Assert.assertEquals(0, task2.getTaskListenerCount());

    }

    private class FakeTask extends AbstractTask {

        public int nbIterations = 100; // count
        public int iterationDuration = 10; // ms

        public FakeTask(String name, int nbIteration, int iterationDuration) {
            super(name);
            this.nbIterations = nbIteration;
            this.iterationDuration = iterationDuration;
        }

        @Override
        public boolean isProgressable() {
            return true;
        }

        @Override
        public boolean isPausable() {
            return false;
        }

        @Override
        public boolean isStoppable() {
            return false;
        }

        @Override
        public void run() {
            this.setState(TaskState.INITIALIZING);
            this.setState(TaskState.RUNNING);
            for (int n = 0; n < this.nbIterations; n++) {
                this.setProgress(n / (double) this.nbIterations);
                System.out.println("[" + this.getName() + "] progress value = "
                        + this.getProgress() + " state = " + this.getState());
                try {
                    Thread.sleep(this.iterationDuration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.setProgress(1.);
            this.setState(TaskState.FINALIZING);
            this.setState(TaskState.FINISHED);
        }
    }

}
