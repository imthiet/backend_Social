import { useState, useEffect, useRef } from 'react'
import Link from 'next/link'
import Image from 'next/image'
import { Bell, Home, MessageSquare, Search, Settings } from 'lucide-react'

interface HeaderProps {
    username: string
}

export default function Header({ username }: HeaderProps) {
    const [isDropdownOpen, setIsDropdownOpen] = useState(false)
    const dropdownRef = useRef<HTMLDivElement>(null)

    useEffect(() => {
        function handleClickOutside(event: MouseEvent) {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
                setIsDropdownOpen(false)
            }
        }

        document.addEventListener('mousedown', handleClickOutside)
        return () => {
            document.removeEventListener('mousedown', handleClickOutside)
        }
    }, [])

    const confirmLogout = (event: React.MouseEvent) => {
        event.preventDefault()
        const confirmed = window.confirm("Are you sure you want to logout?")
        if (confirmed) {
            window.location.href = "/users/logout"
        }
    }

    return (
        <header className="fixed top-0 left-0 w-full bg-white shadow-md z-50 px-4 py-2">
            <div className="max-w-6xl mx-auto flex items-center justify-between">
                <h3 className="text-xl font-bold text-[#00285A]">Now Feeds</h3>

                <nav className="flex items-center space-x-6">
                    <Link href="/newsfeed" className="text-gray-600 hover:text-[#00285A] transition-colors">
                        <Home className="w-6 h-6" />
                        <span className="sr-only">Home</span>
                    </Link>
                    <Link href="/messages" className="text-gray-600 hover:text-[#00285A] transition-colors">
                        <MessageSquare className="w-6 h-6" />
                        <span className="sr-only">Messages</span>
                    </Link>
                    <Link href="/newsfeed" className="text-gray-600 hover:text-[#00285A] transition-colors">
                        <Bell className="w-6 h-6" />
                        <span className="sr-only">Notifications</span>
                    </Link>
                    <Link href="/users" className="text-gray-600 hover:text-[#00285A] transition-colors">
                        <Settings className="w-6 h-6" />
                        <span className="sr-only">Settings</span>
                    </Link>
                    <Link href="/search_page" className="text-gray-600 hover:text-[#00285A] transition-colors">
                        <Search className="w-6 h-6" />
                        <span className="sr-only">Search</span>
                    </Link>
                </nav>

                <div className="relative" ref={dropdownRef}>
                    <button
                        onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                        className="flex items-center space-x-2 text-gray-700 hover:text-[#00285A] transition-colors"
                    >
                        <Image
                            src="/Image/profile.png"
                            alt="User Avatar"
                            width={40}
                            height={40}
                            className="rounded-full"
                        />
                        <span>{username}</span>
                    </button>

                    {isDropdownOpen && (
                        <div className="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1">
                            <Link href="/profile" className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                                Profile
                            </Link>
                            <a
                                href="#"
                                onClick={confirmLogout}
                                className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                            >
                                Logout
                            </a>
                        </div>
                    )}
                </div>
            </div>
        </header>
    )
}