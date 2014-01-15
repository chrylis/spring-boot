package org.springframework.boot.cli.it.infrastructure;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Andy Wilkinson
 */
public final class CliInvocation {

	private final StringBuffer errorOutput = new StringBuffer();

	private final StringBuffer standardOutput = new StringBuffer();

	private final Process process;

	CliInvocation(Process process) {
		this.process = process;

		new Thread(new StreamReadingRunnable(this.process.getErrorStream(),
				this.errorOutput)).start();
		new Thread(new StreamReadingRunnable(this.process.getInputStream(),
				this.standardOutput)).start();
	}

	public String getErrorOutput() {
		return this.errorOutput.toString();
	}

	public String getStandardOutput() {
		return this.standardOutput.toString();
	}

	public int await() throws InterruptedException {
		return this.process.waitFor();
	}

	private final class StreamReadingRunnable implements Runnable {

		private final InputStream stream;

		private final StringBuffer output;

		private final byte[] buffer = new byte[4096];

		private StreamReadingRunnable(InputStream stream, StringBuffer buffer) {
			this.stream = stream;
			this.output = buffer;
		}

		public void run() {
			int read;
			try {
				while ((read = this.stream.read(this.buffer)) > 0) {
					this.output.append(new String(this.buffer, 0, read));
				}
			}
			catch (IOException e) {
				// Allow thread to die
			}
		}
	}

}
