/****************************************************************************
 * Copyright (c) 2008 Mustafa K. Isik and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Mustafa K. Isik - conflict resolution via operational transformations
 *****************************************************************************/

package org.eclipse.ecf.docshare.cola;

import org.eclipse.ecf.docshare.messages.UpdateMessage;

public class ColaUpdateMessage extends UpdateMessage {

	private static final long serialVersionUID = 2038025022180647210L;

	// TODO encapsulate in a new ColaOpOriginationState and re-implement equals,
	// hashCode, i.e. make comparable
	final double localOperationsCount;
	final double remoteOperationsCount;
	final TransformationStrategy trafoStrat;

	public ColaUpdateMessage(UpdateMessage msg, double localOperationsCount, double remoteOperationsCount) {
		super(msg.getOffset(), msg.getLength(), msg.getText());
		this.localOperationsCount = localOperationsCount;
		this.remoteOperationsCount = remoteOperationsCount;
		if (super.getLength() == 0) {
			// this is neither a replacement, nor a deletion
			trafoStrat = new ColaInsertion();
		} else {
			if (super.getText().length() == 0) {
				// something has been replaced, nothing inserted, must be a
				// deletion
				trafoStrat = new ColaDeletion();
			} else {
				// something has been replaced with some new input, has to be a
				// replacement op
				trafoStrat = new ColaReplacement();
			}
		}
	}

	public double getLocalOperationsCount() {
		return this.localOperationsCount;
	}

	public double getRemoteOperationsCount() {
		return this.remoteOperationsCount;
	}

	/**
	 * The receiver of this message transforms it for local application.
	 * 
	 * The transformation assumes that this operation is to be transformed
	 * against queued document owner operations which have a higher modification
	 * priority. that is when in direct index conflict are applied to lower
	 * index positions. This <code>ColaUpdateMessage</code> is transformed to
	 * be applied to the next appropriate higher document index position.
	 * 
	 * @param msg
	 *            queued up operation of local editing site
	 * @return message suitable to be transformed against next queued up
	 *         operation
	 */
	public ColaUpdateMessage transformForApplicationAtOwnerAgainst(ColaUpdateMessage msg) {
		// case this is the operation of lesser insertion priority
		ColaUpdateMessage transformedMsg = trafoStrat.getForOwner(this, msg);
		return transformedMsg;
	}

	public ColaUpdateMessage transformForApplicationAtParticipantAgainst(ColaUpdateMessage msg) {
		// case this is the operation of higher insertion priority
		ColaUpdateMessage transformedMsg = trafoStrat.getForParticipant(this, msg);
		return transformedMsg;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString());
		sb.append(";").append("originationCount.local=").append(this.localOperationsCount).append(";"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		sb.append("originationCount.remote=").append(this.remoteOperationsCount); //$NON-NLS-1$
		return sb.toString();
	}

}
