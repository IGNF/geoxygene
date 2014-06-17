package fr.ign.cogit.geoxygene.appli.task;

import junit.framework.Assert;

import org.junit.Test;

public class TaskManagerTest {

    @Test
    public void testWait() {
        TaskManager manager = new TaskManager();
        FakeTask task = new FakeTask("fake task 1");
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

    private class FakeTask extends AbstractTask {

        public static final int NB_ITERATIONS = 100; // count
        public static final int ITERATION_TIME = 100; // ms

        public FakeTask(String name) {
            super(name);
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
            for (int n = 0; n < NB_ITERATIONS; n++) {
                this.setProgress(n / (double) NB_ITERATIONS);
                System.out.println("[" + this.getName() + "] progress value = "
                        + this.getProgress() + " state = " + this.getState());
                try {
                    Thread.sleep(ITERATION_TIME);
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
