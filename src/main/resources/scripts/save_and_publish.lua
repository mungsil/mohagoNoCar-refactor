-- KEYS[1] : 상태 저장용 Redis 키 (예: travelCourse:spotOptimized:entryStatus:{travelCourseId})
-- KEYS[2] : 스트림 키 (예: travelCourse:spotOptimized:stream)
-- ARGV[1] : travelCourseId
-- ARGV[2] : status 값 (예: CREATED)
-- ARGV[3] : 이벤트 JSON 문자열

-- 1. 상태 저장
redis.call('HSET', KEYS[1], 'travelCourseId', ARGV[1], 'status', ARGV[2])

-- 2. 스트림 발행
local streamId = redis.call('XADD', KEYS[2], '*',
    'travelCourseId', ARGV[1],
    'event', ARGV[3]
)

return streamId