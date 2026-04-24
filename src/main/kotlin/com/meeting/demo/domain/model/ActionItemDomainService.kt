package com.meeting.demo.domain.model

import com.meeting.demo.domain.exception.InvalidStateTransitionException

class ActionItemDomainService {

    fun pickUp(actionItem: ActionItem, assigneeUserId: Long, userStatus: UserStatus) {
        if (userStatus != UserStatus.PLANNING)
            throw InvalidStateTransitionException("PLANNING 상태의 유저만 액션아이템을 픽업할 수 있습니다.")
        actionItem.pickUp(assigneeUserId)
    }
}
