package au.edu.rmit.elevator;

import sun.misc.ConditionLock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

public class StepHandler {
	static Condition trigger;
	static Lock lock;
	static {
		lock = new ReentrantLock();
		trigger = lock.newCondition();
	}

	static void trigger() {
		lock.lock();
		trigger.signalAll();
		lock.unlock();
	}

	static void step(long timeout) {
		lock.lock();
		try {
			boolean to;
			do {
				if (timeout > 0) {
					to = !trigger.await(timeout, TimeUnit.MILLISECONDS);
				} else {
					to = !trigger.await(500, TimeUnit.MILLISECONDS);
				}
			} while (!to);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
}
